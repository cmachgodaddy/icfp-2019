package icfp2019.analyzers

import icfp2019.Direction
import icfp2019.loadProblem
import icfp2019.model.DrillState
import icfp2019.model.GameState
import icfp2019.model.Point
import icfp2019.model.RobotId
import icfp2019.parseDesc
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CalculateDrillCostTest {

    @Test
    fun testDrillCount() {
        val problem3Input = loadProblem(3)
        val problem = parseDesc(problem3Input, "Test")
        val gameState = GameState(problem).withRobotPosition(RobotId.first, Point.origin())
        val drillCost = CalculateDrillCost.analyze(gameState)(RobotId.first, gameState)
        val expected = listOf(
            listOf(
                DrillState(Direction.R, 18),
                DrillState(Direction.L, 1),
                DrillState(Direction.U, 22),
                DrillState(Direction.D, 1)
            )
        )

        Assertions.assertIterableEquals(expected[0], drillCost[0])
    }
}
