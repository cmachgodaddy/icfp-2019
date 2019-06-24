package icfp2019

import icfp2019.analyzers.DistanceToWalls
import icfp2019.model.GameState
import icfp2019.model.Point
import icfp2019.model.RobotId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DistanceToWallsTests {
    @Test
    fun testSimple() {
        val problem3Input = loadProblem(3)
        val p = parseDesc(problem3Input, "Test")
        val robotId = RobotId.first

        val gs1 = GameState(p).withRobotPosition(robotId, Point(20, 0))
        val analyzer1 = DistanceToWalls().analyze(gs1)
        val r1 = analyzer1(robotId, gs1)
        Assertions.assertEquals(8, r1.value)

        val gs2 = GameState(p).withRobotPosition(robotId, Point(22, 0))
        val analyzer2 = DistanceToWalls().analyze(gs1)
        val r2 = analyzer2(robotId, gs2)
        Assertions.assertEquals(6, r2.value)
    }
}
