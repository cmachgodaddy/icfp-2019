package icfp2019.model

data class RobotState(
    val robotId: RobotId,
    val currentPosition: Point,
    val orientation: Orientation = Orientation.Up,
    val remainingFastWheelTime: Int? = null,
    val remainingDrillTime: Int? = null
)
