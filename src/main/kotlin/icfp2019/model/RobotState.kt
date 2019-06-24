package icfp2019.model

import kotlin.math.PI
import kotlin.math.roundToInt

data class RobotState(
    val robotId: RobotId,
    val currentPosition: Point,
    val orientation: Orientation = Orientation.Right,
    val remainingFastWheelTime: Int = 0,
    val remainingDrillTime: Int = 0,
    val armRelativePoints: List<Point> = listOf(Point(1, 0), Point(1, 1), Point(1, -1))
) {
    fun optimumManipulatorArmTarget(): Point {
        val cuddlePoint = Point(-1, 0)
        if (!armRelativePoints.contains(cuddlePoint)) {
            return cuddlePoint
        } else {
            // Assume all arm extensions extend the original T
            var balancePoint = 0
            var pointCount = 0
            armRelativePoints.forEach {
                if (it.x == 1) {
                    balancePoint += it.y
                    pointCount++
                }
            }

            var point = Point(1, -(pointCount + 1) / 2)
            if (balancePoint <= 0) {
                point = Point(1, (pointCount + 1) / 2)
            }
            return point
        }
    }

    fun hasActiveDrill(): Boolean {
        return remainingDrillTime > 1
    }

    fun hasActiveFastWheels(): Boolean {
        return remainingFastWheelTime > 1
    }

    fun turnClockwise(): List<Point> {
        return armRelativePoints
            .map { rotatePoint(it, PI / 2) }
    }

    fun turnCounterClockwise(): List<Point> {
        return armRelativePoints
            .map { rotatePoint(it, -PI / 2) }
    }

    fun rotatePoint(point: Point, theta: Double): Point {
        val x = point.x
        val y = point.y
        return Point(
            ((Math.cos(theta) * x - Math.sin(theta) * y).roundToInt()),
            ((Math.cos(theta) * y + Math.sin(theta) * x).roundToInt())
        )
    }
}
