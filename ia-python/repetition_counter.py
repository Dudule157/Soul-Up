import math

from exercise_rules import SQUAT_DOWN_ANGLE, SQUAT_UP_ANGLE


class SquatRepetitionCounter:
    def __init__(self):
        self.repetitions = 0
        self.state = "up"
        self.lowest_angle = None

    def update(self, knee_angle: float) -> None:
        if knee_angle is None or math.isnan(knee_angle):
            return

        if self.lowest_angle is None or knee_angle < self.lowest_angle:
            self.lowest_angle = knee_angle

        if self.state == "up" and knee_angle <= SQUAT_DOWN_ANGLE:
            self.state = "down"
            return

        if self.state == "down" and knee_angle >= SQUAT_UP_ANGLE:
            self.repetitions += 1
            self.state = "up"
            self.lowest_angle = None
