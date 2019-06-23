package icfp2019.analyzers

import icfp2019.core.applyAction
import icfp2019.model.Action
import icfp2019.model.GameState
import icfp2019.model.RobotId
import icfp2019.toProblem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DFSAnalyzerTest {
    @Test
    fun `get rid of being stuck`() {
        val problem = """
                            XXXXXXXX
                            XXXXwX.X
                            XXXXwX.X
                            Xw@wwX.X
                            XXXX...X
                            XXXXXXXX
                        """.toProblem()

        val startingState = GameState(problem)

        val actions = DFSAnalyzer
            .analyze(startingState)
            .invoke(RobotId.first, startingState)

        Assertions.assertEquals(
            Action.MoveRight,
            actions
        )
    }

    @Test
    fun `get rid of being stuck 3`() {
        val problem = """
                            XXXXXXXX
                            XXXXwX.X
                            XXXXwX.X
                            Xww@wX.X
                            XXXX...X
                            XXXXXXXX
                        """.toProblem()

        val startingState = GameState(problem)

        val actions = DFSAnalyzer
            .analyze(startingState)
            .invoke(RobotId.first, startingState)

        Assertions.assertEquals(
            Action.MoveRight,
            actions
        )
    }

    @Test
    fun `verify dfs movement`() {
        val problem = """
                            XXXXXXXX
                            XXXXwX.X
                            XXXXwX.X
                            Xwww@X.X
                            XXXX...X
                            XXXXXXXX
                        """.toProblem()

        val startingState = GameState(problem)

        val actions = DFSAnalyzer
            .analyze(startingState)
            .invoke(RobotId.first, startingState)

        Assertions.assertEquals(
            Action.MoveDown,
            actions
        )
    }

    @Test
    fun testDfsAnalyzerTest() {
        val map3x2 = """
            ...
            ...
        """.toProblem()
        val startState = GameState(map3x2)
        val analyzer = DFSAnalyzer.analyze(startState)
        val startingWalkState: Pair<GameState, List<Action>> = startState to listOf()
        val result: Pair<GameState, List<Action>> = generateSequence(startingWalkState) { (state, actions) ->
            if (state.isGameComplete()) null
            else {
                val action = analyzer.invoke(RobotId.first, state)
                val newState = applyAction(state, RobotId.first, action)
                newState to actions.plus(action)
            }
        }.last()
        println(result.second)
    }
}
