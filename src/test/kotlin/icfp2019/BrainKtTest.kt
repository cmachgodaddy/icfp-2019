package icfp2019

import icfp2019.core.Strategy
import icfp2019.model.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

import java.util.*

internal class BrainKtTest {
    class TestStrategy(vararg actions: Action) : Strategy {
        private val queue = ArrayDeque(actions.toList())

        override fun compute(initialState: GameState): (robotId: RobotId, state: GameState) -> Action {
            return { _, _ ->
                if (queue.isEmpty()) {
                    Action.DoNothing
                } else {
                    queue.pop()
                }
            }
        }
    }

    @Test
    @Disabled
    fun brainStep() {
        val problem = parseTestMap(init)
        val solution = parseTestMap(fini)
        printBoard(problem)
        var state = GameState.gameStateOf(problem)
        val strategies = listOf(
            TestStrategy(Action.MoveDown, Action.DoNothing, Action.MoveDown),
            TestStrategy(Action.DoNothing, Action.MoveRight))
        for (i in 0..3) {
            val (result, actions) = brainStep(
                state,
                strategies,
                1
            )

            state = result

            println(actions)
            val problem0 = problem.copy(map = result.cells)
            printBoard(problem0)
        }

        Assertions.assertEquals(solution.map, state.cells)
    }

    private val init =
        """
        @..
        ...
        ...
        """

    private val fini =
        """
        @..
        www
        ..w
        """
}
