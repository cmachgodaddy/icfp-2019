package icfp2019.model

data class RobotState(
    val robotId: RobotId,
    val currentPosition: Point,
    val orientation: Orientation = Orientation.Up,
    val remainingFastWheelTime: Int = 0,
    val remainingDrillTime: Int = 0
) {
    fun hasActiveDrill(): Boolean {
        return remainingDrillTime > 0
    }

    fun hasActiveFastWheels(): Boolean {
        return remainingFastWheelTime > 0
    }
}
