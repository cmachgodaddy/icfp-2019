package icfp2019.core

import icfp2019.model.*
import icfp2019.printBoard
import icfp2019.toProblem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ActionStateTransitionEngineKtTests {

    @Test
    fun verifyMovements() {
        val problem = """
                        ..
                        @.
                    """.toProblem()
        val startingPosition = problem.startingPosition
        val startingState = GameState(problem)
        val upRightState = applyAction(startingState, RobotId.first, Action.MoveUp).let {
            applyAction(it, RobotId.first, Action.MoveRight)
        }

        Assertions.assertEquals(
            startingPosition.up().right(),
            upRightState.robotState.getValue(RobotId.first).currentPosition
        )

        val backToOrigin = applyAction(upRightState, RobotId.first, Action.MoveDown).let {
            applyAction(it, RobotId.first, Action.MoveLeft)
        }

        Assertions.assertEquals(
            startingPosition,
            backToOrigin.robotState.getValue(RobotId.first).currentPosition
        )
    }

    @Test
    fun verifyPickupBooster() {

        val problem = "l@".toProblem()
        val gameState = GameState(problem)

        Assertions.assertEquals(
            Node(Point.origin(), isObstacle = false, booster = Booster.Drill),
            gameState.get(Point.origin())
        )

        listOf(Action.MoveLeft).applyTo(gameState).let {
            Assertions.assertEquals(mapOf(Booster.Drill to 1), it.unusedBoosters)
            Assertions.assertEquals(
                Node(Point.origin(), isObstacle = false, isWrapped = true, booster = null),
                it.get(Point.origin())
            )
        }
    }

    @Test
    fun verifyArmsAttach() {

        val problem = "b@".toProblem()
        val gameState = GameState(problem)

        Assertions.assertEquals(
            Node(Point.origin(), isObstacle = false, booster = Booster.ExtraArm),
            gameState.get(Point.origin())
        )

        val pickupState = listOf(Action.MoveLeft).applyTo(gameState)
        pickupState.run {
            Assertions.assertEquals(1, boostersAvailable(Booster.ExtraArm))
            Assertions.assertEquals(mapOf(Booster.ExtraArm to 1), this.unusedBoosters)
            Assertions.assertEquals(
                Node(Point.origin(), isObstacle = false, isWrapped = true, booster = null),
                this.get(Point.origin())
            )
        }

        val applyTo = listOf(Action.AttachManipulator(Point(2, 0))).applyTo(pickupState)
        applyTo.run {
            Assertions.assertEquals(0, boostersAvailable(Booster.ExtraArm))
            val robot = robot(RobotId.first)
            Assertions.assertEquals(
                listOf(Point(1, 0), Point(1, 1), Point(1, -1), Point(2, 0)),
                robot.armRelativePoints
            )
        }
    }

    @Test
    fun verifyFastMove() {

        val problem = """
        ...XX
        f....
        @..XX
    """.toProblem()
        val gameState = GameState(problem)

        val actions = listOf(
            Action.MoveUp, Action.AttachFastWheels, Action.MoveRight, Action.MoveRight
        )
        val expectedProblem = """
        .wwXX
        wwwww
        wwwXX
    """.toProblem()

        actions.applyTo(gameState).let { state ->
            printBoard(state)
            Assertions.assertEquals(expectedProblem.map, state.cells)
        }
    }

    @Test
    fun verifyDrill() {

        val problem = """
        ..X..
        @lX..
        ..X..
    """.toProblem()
        val gameState = GameState(problem)

        val actions = listOf(
            Action.MoveRight, Action.StartDrill, Action.MoveRight, Action.MoveRight, Action.MoveRight
        )
        val expectedProblem = """
        ..Xww
        wwwww
        ..Xww
    """.toProblem()

        actions.applyTo(gameState).let { state ->
            printBoard(state)
            Assertions.assertEquals(expectedProblem.map, state.cells)
        }
    }

    @Test
    fun verifyTeleport() {

        val problem = """
        .....
        r....
        @....
    """.toProblem()
        val gameState = GameState(problem)

        val actions = listOf(
            Action.MoveUp, Action.MoveUp, Action.MoveRight, Action.MoveRight,
            Action.MoveRight, Action.MoveRight, Action.PlantTeleportResetPoint,
            Action.MoveDown, Action.MoveDown, Action.MoveLeft, Action.MoveLeft,
            Action.TeleportBack(Point(4, 2)), Action.MoveLeft, Action.MoveDown,
            Action.MoveLeft, Action.MoveLeft, Action.MoveDown
        )
        val expectedProblem = """
        wwww*
        wwwww
        wwwww
    """.toProblem()

        actions.applyTo(gameState).let { state ->
            printBoard(state)
            Assertions.assertEquals(expectedProblem.map, state.cells)
        }
    }

    @Test
    fun verifyWrapping() {

        val problem = """
        ...XX
        .....
        @..XX
    """.toProblem()
        val gameState = GameState(problem)

        val actions = listOf(
            Action.MoveUp, Action.MoveUp,
            Action.MoveRight, Action.MoveDown, Action.MoveDown,
            Action.MoveRight, Action.MoveUp, Action.MoveUp,
            Action.MoveDown
        )
        val expectedProblem = """
        wwwXX
        wwww.
        wwwXX
    """.toProblem()

        actions.applyTo(gameState).let {
            Assertions.assertEquals(expectedProblem.map, it.cells)
        }
    }

    private fun List<Action>.applyTo(gameState: GameState): GameState {
        return this.fold(gameState) { state, action ->
            applyAction(state, RobotId.first, action)
        }
    }
}
