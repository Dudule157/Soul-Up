import cv2

from exercise_rules import SQUAT_EXERCISE_NAME, build_feedback
from pose_detector import PoseDetector
from repetition_counter import SquatRepetitionCounter


class VideoProcessor:
    def __init__(self):
        self.pose_detector = PoseDetector()

    def process(self, video_path: str) -> dict:
        capture = cv2.VideoCapture(video_path)
        if not capture.isOpened():
            raise ValueError("Nao foi possivel abrir o video informado.")

        fps = capture.get(cv2.CAP_PROP_FPS) or 0
        total_frames = int(capture.get(cv2.CAP_PROP_FRAME_COUNT) or 0)
        duration = int(round(total_frames / fps)) if fps > 0 else 0

        counter = SquatRepetitionCounter()
        analyzed_frames = 0
        frames_with_pose = 0

        try:
            while True:
                success, frame = capture.read()
                if not success:
                    break

                analyzed_frames += 1
                knee_angle = self.pose_detector.detect_knee_angle(frame)

                if knee_angle is not None:
                    frames_with_pose += 1
                    counter.update(knee_angle)
        finally:
            capture.release()
            self.pose_detector.close()

        return {
            "exercise": SQUAT_EXERCISE_NAME,
            "repetitions": counter.repetitions,
            "duration": duration,
            "feedback": build_feedback(counter.repetitions, analyzed_frames, frames_with_pose),
        }
