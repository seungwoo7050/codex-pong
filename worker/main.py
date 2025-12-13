import hashlib
import os
import subprocess
import time

import redis

REDIS_HOST = os.getenv("REDIS_HOST", "redis")
REDIS_PORT = int(os.getenv("REDIS_PORT", "6379"))
REQUEST_STREAM = os.getenv("JOB_QUEUE_REQUEST_STREAM", "job.requests")
PROGRESS_STREAM = os.getenv("JOB_QUEUE_PROGRESS_STREAM", "job.progress")
RESULT_STREAM = os.getenv("JOB_QUEUE_RESULT_STREAM", "job.results")
CONSUMER_GROUP = os.getenv("JOB_QUEUE_CONSUMER_GROUP", "replay-jobs")
CONSUMER_NAME = os.getenv("WORKER_ID", "replay-worker")

r = redis.Redis(host=REDIS_HOST, port=REDIS_PORT, decode_responses=True)


def ensure_group():
    try:
        r.xgroup_create(REQUEST_STREAM, CONSUMER_GROUP, id="0", mkstream=True)
    except redis.ResponseError as exc:
        if "BUSYGROUP" not in str(exc):
            raise


def publish_progress(job_id: str, progress: int, phase: str, message: str):
    r.xadd(PROGRESS_STREAM, {
        "jobId": job_id,
        "progress": str(progress),
        "phase": phase,
        "message": message,
    })


def publish_result(job_id: str, status: str, result_uri: str = "", checksum: str = "",
                   error_code: str = "", error_message: str = ""):
    r.xadd(RESULT_STREAM, {
        "jobId": job_id,
        "status": status,
        "resultUri": result_uri,
        "checksum": checksum,
        "errorCode": error_code,
        "errorMessage": error_message,
    })


def checksum_file(path: str) -> str:
    h = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(8192), b""):
            h.update(chunk)
    return h.hexdigest()


def run_ffmpeg(job_id: str, command: list[str], expected_ms: int, phase: str):
    os.makedirs(os.path.dirname(command[-1]), exist_ok=True)
    publish_progress(job_id, 5, "PREPARE", "ffmpeg 실행 준비 중")
    process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    try:
        for line in process.stdout:
            line = line.strip()
            if line.startswith("out_time_ms") and expected_ms > 0:
                try:
                    out_ms = int(line.split("=")[1])
                    percent = min(99, int(out_ms / expected_ms * 100))
                    publish_progress(job_id, percent, phase, "인코딩 진행 중")
                except ValueError:
                    continue
            if line.startswith("progress=end"):
                publish_progress(job_id, 99, phase, "마무리 중")
    finally:
        process.wait()
        if process.returncode != 0:
            raise RuntimeError(f"ffmpeg 실패: {process.returncode}")


def export_mp4(job_id: str, replay_id: str, options: dict):
    output_path = options.get("outputPath")
    duration_ms = int(options.get("durationMs", "0"))
    duration_sec = max(duration_ms / 1000, 1)
    if output_path and os.path.exists(output_path):
        checksum = checksum_file(output_path)
        publish_result(job_id, "SUCCEEDED", output_path, checksum)
        return
    command = [
        "ffmpeg", "-y",
        "-f", "lavfi", "-i", f"color=c=black:s=1280x720:d={duration_sec}",
        "-vf", f"drawtext=text='Replay {replay_id}':fontcolor=white:fontsize=48:x=(w-text_w)/2:y=(h-text_h)/2",
        "-c:v", "libx264", "-pix_fmt", "yuv420p",
        "-progress", "pipe:1",
        output_path,
    ]
    run_ffmpeg(job_id, command, int(duration_ms if duration_ms > 0 else duration_sec * 1000), "ENCODE")
    checksum = checksum_file(output_path)
    publish_result(job_id, "SUCCEEDED", output_path, checksum)


def export_thumbnail(job_id: str, replay_id: str, options: dict):
    output_path = options.get("outputPath")
    duration_ms = int(options.get("durationMs", "0"))
    still_sec = max(duration_ms / 2000, 1)
    if output_path and os.path.exists(output_path):
        checksum = checksum_file(output_path)
        publish_result(job_id, "SUCCEEDED", output_path, checksum)
        return
    command = [
        "ffmpeg", "-y",
        "-f", "lavfi", "-i", f"color=c=blue:s=640x360:d={still_sec}",
        "-frames:v", "1",
        "-progress", "pipe:1",
        output_path,
    ]
    run_ffmpeg(job_id, command, int(still_sec * 1000), "THUMBNAIL")
    checksum = checksum_file(output_path)
    publish_result(job_id, "SUCCEEDED", output_path, checksum)


def process_request(fields: dict):
    job_id = fields.get("jobId")
    job_type = fields.get("jobType")
    replay_id = fields.get("replayId", "")
    options = {k: v for k, v in fields.items() if k not in {"jobId", "jobType", "replayId"}}
    if not job_id or not job_type:
        return
    try:
        publish_progress(job_id, 10, "QUEUE", "워커가 작업을 시작했습니다")
        if job_type == "REPLAY_EXPORT_MP4":
            export_mp4(job_id, replay_id, options)
        elif job_type == "REPLAY_THUMBNAIL":
            export_thumbnail(job_id, replay_id, options)
        else:
            publish_result(job_id, "FAILED", error_code="UNSUPPORTED_TYPE", error_message=f"지원하지 않는 유형 {job_type}")
    except Exception as exc:  # noqa: BLE001
        publish_result(job_id, "FAILED", error_code="WORKER_ERROR", error_message=str(exc))


def main_loop():
    ensure_group()
    while True:
        messages = r.xreadgroup(CONSUMER_GROUP, CONSUMER_NAME, {REQUEST_STREAM: ">"}, count=1, block=5000)
        if not messages:
            continue
        for _, items in messages:
            for message_id, fields in items:
                process_request(fields)
                r.xack(REQUEST_STREAM, CONSUMER_GROUP, message_id)
                time.sleep(0.1)


if __name__ == "__main__":
    main_loop()
