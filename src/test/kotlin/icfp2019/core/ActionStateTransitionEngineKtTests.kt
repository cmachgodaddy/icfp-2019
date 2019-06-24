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
            upRightState.robot(RobotId.first).currentPosition
        )

        val backToOrigin = applyAction(upRightState, RobotId.first, Action.MoveDown).let {
            applyAction(it, RobotId.first, Action.MoveLeft)
        }

        Assertions.assertEquals(
            startingPosition,
            backToOrigin.robot(RobotId.first).currentPosition
        )
    }

    @Test
    fun verifyPickupBooster() {

        val problem = "l@".toProblem()
        val gameState = GameState(problem)

        Assertions.assertEquals(
            BoardNodeState(Point.origin(), isWrapped = false, booster = Booster.Drill),
            gameState.nodeState(Point.origin())
        )

        listOf(Action.MoveLeft).applyTo(gameState).let {
            Assertions.assertEquals(mapOf(Booster.Drill to 1), it.unusedBoosters)
            Assertions.assertEquals(
                BoardNodeState(Point.origin(), isWrapped = true),
                it.nodeState(Point.origin())
            )
        }
    }

    @Test
    fun verifyArmsAttach() {

        val problem = "b@".toProblem()
        val gameState = GameState(problem)

        Assertions.assertEquals(
            BoardNodeState(Point.origin(), booster = Booster.ExtraArm),
            gameState.nodeState(Point.origin())
        )

        val pickupState = listOf(Action.MoveLeft).applyTo(gameState)
        pickupState.run {
            Assertions.assertEquals(1, boostersAvailable(Booster.ExtraArm))
            Assertions.assertEquals(mapOf(Booster.ExtraArm to 1), this.unusedBoosters)
            Assertions.assertEquals(
                BoardNodeState(Point.origin(), isWrapped = true, booster = null),
                this.nodeState(Point.origin())
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
            Assertions.assertEquals(expectedProblem.map, state.toProblem().map)
        }
    }

    // THIS IS BROKEN
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
        wwX..
        ..X..
    """.toProblem()

        actions.applyTo(gameState).let { state ->
            printBoard(state)
            Assertions.assertEquals(expectedProblem.map, state.toProblem().map)
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
            Assertions.assertEquals(expectedProblem.map, state.toProblem().map)
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
            printBoard(gameState)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArm() {

        val problem = """
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        @....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, 2), Point(1, 3), Point(1, 4), Point(1, 5), Point(1, 6), Point(1, 7), Point(1, 8), Point(1, 9), Point(1, 10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        .....
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        www..
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArm1() {

        val problem = """
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .X...
        @....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, 2), Point(1, 3), Point(1, 4), Point(1, 5), Point(1, 6), Point(1, 7), Point(1, 8), Point(1, 9), Point(1, 10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .Xw..
        www..
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArm2() {

        val problem = """
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .X...
        .....
        @....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, 2), Point(1, 3), Point(1, 4), Point(1, 5), Point(1, 6), Point(1, 7), Point(1, 8), Point(1, 9), Point(1, 10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        ..w..
        .Xw..
        ..w..
        www..
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArm3() {

        val problem = """
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .X...
        .....
        .....
        @....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, 2), Point(1, 3), Point(1, 4), Point(1, 5), Point(1, 6), Point(1, 7), Point(1, 8), Point(1, 9), Point(1, 10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        .....
        .....
        .....
        .....
        .....
        .....
        ..w..
        ..w..
        .Xw..
        ..w..
        ..w..
        www..
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArm4() {

        val problem = """
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .X...
        .....
        .....
        .....
        @....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, 2), Point(1, 3), Point(1, 4), Point(1, 5), Point(1, 6), Point(1, 7), Point(1, 8), Point(1, 9), Point(1, 10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        .....
        .....
        .....
        .....
        ..w..
        ..w..
        ..w..
        .Xw..
        ..w..
        ..w..
        ..w..
        www..
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArm5() {

        val problem = """
        .....
        .....
        .....
        .....
        .....
        .....
        .X...
        .....
        .....
        .....
        .....
        @....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, 2), Point(1, 3), Point(1, 4), Point(1, 5), Point(1, 6), Point(1, 7), Point(1, 8), Point(1, 9), Point(1, 10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        .....
        .....
        ..w..
        ..w..
        ..w..
        ..w..
        .Xw..
        ..w..
        ..w..
        ..w..
        ..w..
        www..
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArm6() {

        val problem = """
        .....
        .....
        .....
        .....
        .....
        .X...
        .....
        .....
        .....
        .....
        .....
        @....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, 2), Point(1, 3), Point(1, 4), Point(1, 5), Point(1, 6), Point(1, 7), Point(1, 8), Point(1, 9), Point(1, 10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        .....
        ..w..
        ..w..
        ..w..
        ..w..
        .Xw..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        www..
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArmBottom() {

        val problem = """
        @....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, -2), Point(1, -3), Point(1, -4), Point(1, -5), Point(1, -6), Point(1, -7), Point(1, -8), Point(1, -9), Point(1, -10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        www..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        .....
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArmBottom1() {

        val problem = """
        @....
        .X...
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, -2), Point(1, -3), Point(1, -4), Point(1, -5), Point(1, -6), Point(1, -7), Point(1, -8), Point(1, -9), Point(1, -10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        www..
        .Xw..
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArmBottom2() {

        val problem = """
        @....
        .....
        .X...
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, -2), Point(1, -3), Point(1, -4), Point(1, -5), Point(1, -6), Point(1, -7), Point(1, -8), Point(1, -9), Point(1, -10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        www..
        ..w..
        .Xw..
        ..w..
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArmBottom3() {

        val problem = """
        @....
        .....
        .....
        .X...
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, -2), Point(1, -3), Point(1, -4), Point(1, -5), Point(1, -6), Point(1, -7), Point(1, -8), Point(1, -9), Point(1, -10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        www..
        ..w..
        ..w..
        .Xw..
        ..w..
        ..w..
        .....
        .....
        .....
        .....
        .....
        .....
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArmBottom4() {

        val problem = """
        @....
        .....
        .....
        .....
        .X...
        .....
        .....
        .....
        .....
        .....
        .....
        .....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, -2), Point(1, -3), Point(1, -4), Point(1, -5), Point(1, -6), Point(1, -7), Point(1, -8), Point(1, -9), Point(1, -10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        www..
        ..w..
        ..w..
        ..w..
        .Xw..
        ..w..
        ..w..
        ..w..
        .....
        .....
        .....
        .....
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArmBottom5() {

        val problem = """
        @....
        .....
        .....
        .....
        .....
        .X...
        .....
        .....
        .....
        .....
        .....
        .....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, -2), Point(1, -3), Point(1, -4), Point(1, -5), Point(1, -6), Point(1, -7), Point(1, -8), Point(1, -9), Point(1, -10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        www..
        ..w..
        ..w..
        ..w..
        ..w..
        .Xw..
        ..w..
        ..w..
        ..w..
        ..w..
        .....
        .....
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArmBottom6() {

        val problem = """
        @....
        .....
        .....
        .....
        .....
        .....
        .X...
        .....
        .....
        .....
        .....
        .....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, -2), Point(1, -3), Point(1, -4), Point(1, -5), Point(1, -6), Point(1, -7), Point(1, -8), Point(1, -9), Point(1, -10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        www..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        .Xw..
        ..w..
        ..w..
        ..w..
        ..w..
        .....
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyBoth() {

        val problem = """
        .....
        .....
        .....
        .....
        .....
        .....
        @....
        .....
        .....
        .....
        .....
        .....
        .....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, -2), Point(1, 2), Point(1, -3), Point(1, 3), Point(1, -4), Point(1, 4), Point(1, -5), Point(1, 5))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        .....
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        www..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        .....
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyBoth1() {

        val problem = """
        .....
        .....
        .....
        .....
        .X...
        .....
        @....
        .X...
        .....
        .....
        .....
        .....
        .....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, -2), Point(1, 2), Point(1, -3), Point(1, 3), Point(1, -4), Point(1, 4), Point(1, -5), Point(1, 5))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        .....
        .....
        .....
        ..w..
        .Xw..
        ..w..
        www..
        .Xw..
        .....
        .....
        .....
        .....
        .....
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArmX2() {

        val problem = """
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        ..X..
        .....
        @....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, 2), Point(1, 3), Point(1, 4), Point(1, 5), Point(1, 6), Point(1, 7), Point(1, 8), Point(1, 9), Point(1, 10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        .....
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        ..w..
        .....
        .....
        ..X..
        ..w..
        www..
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyLongArmX3() {

        val problem = """
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        .....
        ..X..
        .....
        .....
        @....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, 2), Point(1, 3), Point(1, 4), Point(1, 5), Point(1, 6), Point(1, 7), Point(1, 8), Point(1, 9), Point(1, 10))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        .....
        ..w..
        ..w..
        ..w..
        ..w..
        .....
        .....
        .....
        ..X..
        ..w..
        ..w..
        www..
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    @Test
    fun verifyBothX1() {

        val problem = """
        .....
        .....
        .....
        .....
        ..X..
        .....
        @....
        ..X..
        .....
        .....
        .....
        .....
        .....
    """.toProblem()
        val originalGameState = GameState(problem)
        val originalRobotState = originalGameState.robot(RobotId.first)
        val newRobotState = originalRobotState.copy(armRelativePoints = originalRobotState.armRelativePoints.plus(listOf(Point(1, -2), Point(1, 2), Point(1, -3), Point(1, 3), Point(1, -4), Point(1, 4), Point(1, -5), Point(1, 5))))
        val gameState = originalGameState.withRobotState(RobotId.first, newRobotState)

        val actions = listOf(
            Action.MoveRight
        )
        val expectedProblem = """
        .....
        ..w..
        .....
        .....
        ..X..
        ..w..
        www..
        ..X..
        .....
        ..w..
        ..w..
        ..w..
        .....
    """.toProblem()

        actions.applyTo(gameState).let {
            printBoard(it)
            Assertions.assertEquals(expectedProblem.map, it.toProblem().map)
        }
    }

    private fun List<Action>.applyTo(gameState: GameState): GameState {
        return this.fold(gameState) { state, action ->
            applyAction(state, RobotId.first, action)
        }
    }
}
