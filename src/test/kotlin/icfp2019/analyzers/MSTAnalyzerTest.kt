package icfp2019.analyzers

import icfp2019.model.*
import icfp2019.toProblem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MSTAnalyzerTest {

    @Test
    fun testMinimumSpanningTree6Nodes() {
        val problem = """
            ..
            ..
            ..
        """.toProblem()

        val gameState = GameState.gameStateOf(problem)

        val spanningTree = MSTAnalyzer
            .analyze(gameState)(RobotId(0), gameState)

        val count = spanningTree.edges.size
        Assertions.assertEquals(count, 5)
    }

    @Test
    fun testMinimumSpanningTree9Nodes() {
        val problem = """
            ...
            ...
            ...
        """.toProblem()

        val gameState = GameState.gameStateOf(problem)

        val spanningTree = MSTAnalyzer
            .analyze(gameState)(RobotId(0), GameState.gameStateOf(Point(0, 0)))

        val count = spanningTree.edges.size
        Assertions.assertEquals(count, 8)
    }
}