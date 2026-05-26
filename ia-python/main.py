import json
import sys
from pathlib import Path

from video_processor import VideoProcessor


def main() -> int:
    if len(sys.argv) != 2:
        print("Uso: python main.py <caminho-do-video>", file=sys.stderr)
        return 1

    video_path = Path(sys.argv[1])
    if not video_path.exists() or not video_path.is_file():
        print(f"Video nao encontrado: {video_path}", file=sys.stderr)
        return 1

    try:
        result = VideoProcessor().process(str(video_path))
        print(json.dumps(result, ensure_ascii=False))
        return 0
    except Exception as exception:
        print(str(exception), file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
