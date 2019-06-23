package icfp2019.analyzers

import icfp2019.model.*
import icfp2019.toProblem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MSTAnalyzerTest {

    @Test
    fun testMinimumSpanningTree6Nodes() {

        val map3x2 = """
            ..@
            ...
        """.toProblem()

        val gameState = GameState(map3x2)

        val spanningTree = MSTAnalyzer.analyze(gameState)(RobotId.first, gameState)

        val count = spanningTree.edges.size
        Assertions.assertEquals(count, 5)
    }

    @Test
    fun testMinimumSpanningTree9Nodes() {
        val map3x3 = """
            ...
            ...
            ...
        """.toProblem()

        val gameState = GameState(map3x3)

        val spanningTree = MSTAnalyzer.analyze(gameState)(RobotId.first, gameState)

        val count = spanningTree.edges.size
        Assertions.assertEquals(count, 8)
    }
}
