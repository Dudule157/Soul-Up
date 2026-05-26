SQUAT_EXERCISE_NAME = "agachamento"

SQUAT_DOWN_ANGLE = 105
SQUAT_UP_ANGLE = 160
MIN_LANDMARK_VISIBILITY = 0.55


def build_feedback(repetitions: int, analyzed_frames: int, frames_with_pose: int) -> str:
    if analyzed_frames == 0:
        return "Video vazio ou invalido."

    pose_rate = frames_with_pose / analyzed_frames

    if frames_with_pose == 0:
        return "Pose corporal nao detectada. Grave o corpo inteiro com boa iluminacao."

    if pose_rate < 0.5:
        return "Pose detectada em poucos frames. Ajuste camera, distancia e iluminacao."

    if repetitions == 0:
        return "Movimento insuficiente para contar repeticoes completas."

    return "Movimento consistente."
