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
            Assertions.assertIterableEquals(listOf(Booster.Drill), it.unusedBoosters)
            Assertions.assertEquals(
                Node(Point.origin(), isObstacle = false, isWrapped = true, booster = null),
                it.get(Point.origin())
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
        ...XX
        wwwww
        w..XX
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
        ..X..
        wwwww
        ..X..
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
            Action.MoveDown, Action.MoveRight
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
