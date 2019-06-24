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
        val map = mapOf(RobotId.first to actions)
        val actualOutput = map.encodeActions()

        Assertions.assertEquals("SSAWWD", actualOutput)
    }

    @Test
    fun testOutputOfTwoRobot() {
        val actions = getListOfAction()
        // Adding two robots
        val map = mapOf(
            RobotId.first to actions,
            RobotId.first.nextId() to actions
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
            RobotId.first to actionPlus,
            RobotId.first.nextId() to actionPlus
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
            RobotId.first to actionPlus,
            RobotId.first.nextId() to actionPlus
        )

        val actualOutput = map.encodeActions()
        Assertions.assertEquals("SSAWWDB(1,2)#SSAWWDB(1,2)", actualOutput)
    }
}
