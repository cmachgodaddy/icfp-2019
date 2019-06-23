package icfp2019

import icfp2019.model.Action
import icfp2019.model.Point
import icfp2019.model.RobotId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class OutputTests {
    fun getListOfAction(): List<Action> {
        return listOf(
            Action.MoveDown,
            Action.MoveDown,
            Action.MoveLeft,
            Action.MoveUp,
            Action.MoveUp,
            Action.MoveRight)
    }

    @Test
    fun testOutputOfOneRobot() {
        val actions = getListOfAction()
        val map = mapOf(RobotId(1) to actions)
        val actualOutput = map.encodeActions()

        Assertions.assertEquals("SSAWWD", actualOutput)
    }

    @Test
    fun testOutputOfTwoRobot() {
        val actions = getListOfAction()
        // Adding two robots
        val map = mapOf(
            RobotId(1) to actions,
            RobotId(2) to actions
        )
        val actualOutput = map.encodeActions()

        Assertions.assertEquals("SSAWWD#SSAWWD", actualOutput)
    }

    @Test
    fun testOutputOfSolutionWithTeleport() {
        val actions = getListOfAction()
        val actionPlus = actions.plus(Action.TeleportBack(Point(1, 2)))

        // Adding two robots
        val map = mapOf(
            RobotId(1) to actionPlus,
            RobotId(2) to actionPlus
        )

        val actualOutput = map.encodeActions()
        Assertions.assertEquals("SSAWWDT(1,2)#SSAWWDT(1,2)", actualOutput)
    }

    @Test
    fun testOutputOfSolutionWithManipulator() {
        val actions = getListOfAction()
        val actionPlus = actions.plus(Action.AttachManipulator(Point(1, 2)))

        // Adding two robots
        val map = mapOf(
            RobotId(1) to actionPlus,
            RobotId(2) to actionPlus
        )

        val actualOutput = map.encodeActions()
        Assertions.assertEquals("SSAWWDA(1,2)#SSAWWDA(1,2)", actualOutput)
    }
}
