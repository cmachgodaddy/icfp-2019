package icfp2019

import icfp2019.core.Strategy
import icfp2019.model.Action
import icfp2019.model.GameState
import icfp2019.model.RobotId
import org.junit.jupiter.api.Assertions
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
    fun brainStep() {
        val problem = parseTestMap(init)
        val solution = parseTestMap(fini)
        printBoard(problem)
        val strategy = TestStrategy(Action.MoveDown, Action.MoveRight)
        var state = GameState(problem)
        for (i in 0..1) {
            val (result, actions) = brainStep(
                state,
                strategy,
                1
            )

            state = result

            println(actions)
            printBoard(result)
        }

        Assertions.assertEquals(solution.map, state.toProblem().map)
    }

    private val init =
        """
        @...
        ....
        ....
        ....
        """

    private val fini =
        """
        www.
        www.
        .ww.
        ....
        """
}
