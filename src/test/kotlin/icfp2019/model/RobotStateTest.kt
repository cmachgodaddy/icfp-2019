package icfp2019.model

import icfp2019.core.applyAction
import icfp2019.toProblem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class RobotStateTest {
    @Test
    fun optimumManipulatorArmTarget() {
        var state = RobotState(RobotId.first, Point(0, 0))
        Assertions.assertEquals(Point(-1, 0), state.optimumManipulatorArmTarget())
        state = state.copy(RobotId.first, armRelativePoints = state.armRelativePoints.plus(Point(-1, 0)))
        Assertions.assertEquals(Point(1, 2), state.optimumManipulatorArmTarget())
        state = state.copy(RobotId.first, armRelativePoints = state.armRelativePoints.plus(Point(1, 2)))
        Assertions.assertEquals(Point(1, -2), state.optimumManipulatorArmTarget())
    }

    @Test
    fun testRotate() {

        val map = """..""".toProblem()
        val startState = GameState(map)

        val robotId = RobotId.first

        val state0 = applyAction(startState, robotId, Action.TurnClockwise)
        Assertions.assertEquals(
            listOf(Point(0, 1), Point(-1, 1), Point(1, 1)),
            state0.robot(robotId).armRelativePoints
        )

        val state1 = applyAction(startState, RobotId.first, Action.TurnCounterClockwise)
        Assertions.assertEquals(
            listOf(Point(0, -1), Point(1, -1), Point(-1, -1)),
            state1.robot(robotId).armRelativePoints
        )
    }

    @Test
    fun testRotate360() {

        val map = """..""".toProblem()
        val startState = GameState(map)
        val robotId = RobotId.first

        val state0 = applyAction(startState, robotId, Action.TurnClockwise)
        val state1 = applyAction(state0, robotId, Action.TurnClockwise)
        val state2 = applyAction(state1, robotId, Action.TurnClockwise)
        val finalState = applyAction(state2, robotId, Action.TurnClockwise)

        Assertions.assertEquals(
            listOf(Point(1, 0), Point(1, 1), Point(1, -1)),
            finalState.robot(robotId).armRelativePoints
        )
    }
}
