import math

import cv2
import mediapipe as mp

from exercise_rules import MIN_LANDMARK_VISIBILITY


class PoseDetector:
    def __init__(self):
        self._pose = mp.solutions.pose.Pose(
            static_image_mode=False,
            model_complexity=1,
            enable_segmentation=False,
            min_detection_confidence=0.5,
            min_tracking_confidence=0.5,
        )
        self._landmark = mp.solutions.pose.PoseLandmark

    def detect_knee_angle(self, frame):
        rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        result = self._pose.process(rgb_frame)

        if not result.pose_landmarks:
            return None

        landmarks = result.pose_landmarks.landmark
        left_angle = self._calculate_side_angle(
            landmarks,
            self._landmark.LEFT_HIP,
            self._landmark.LEFT_KNEE,
            self._landmark.LEFT_ANKLE,
        )
        right_angle = self._calculate_side_angle(
            landmarks,
            self._landmark.RIGHT_HIP,
            self._landmark.RIGHT_KNEE,
            self._landmark.RIGHT_ANKLE,
        )

        angles = [angle for angle in (left_angle, right_angle) if angle is not None]
        if not angles:
            return None

        return sum(angles) / len(angles)

    def close(self):
        self._pose.close()

    def _calculate_side_angle(self, landmarks, hip_index, knee_index, ankle_index):
        hip = landmarks[hip_index.value]
        knee = landmarks[knee_index.value]
        ankle = landmarks[ankle_index.value]

        if not self._is_visible(hip, knee, ankle):
            return None

        return calculate_angle(
            (hip.x, hip.y),
            (knee.x, knee.y),
            (ankle.x, ankle.y),
        )

    @staticmethod
    def _is_visible(*points):
        return all(point.visibility >= MIN_LANDMARK_VISIBILITY for point in points)


def calculate_angle(point_a, point_b, point_c):
    radians = math.atan2(point_c[1] - point_b[1], point_c[0] - point_b[0]) - math.atan2(
        point_a[1] - point_b[1],
        point_a[0] - point_b[0],
    )
    angle = abs(math.degrees(radians))

    if angle > 180:
        angle = 360 - angle

    return angle
