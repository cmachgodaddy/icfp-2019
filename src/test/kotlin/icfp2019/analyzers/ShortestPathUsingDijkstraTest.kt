package icfp2019.analyzers

import icfp2019.model.GameState
import icfp2019.model.Node
import icfp2019.model.Point
import icfp2019.model.RobotId
import icfp2019.toProblem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ShortestPathUsingDijkstraTest {

    @Test
    fun testShortestDistanceToPoint() {
        val problem = """
            ..
            ..
            ..
        """.toProblem()

        val gameState = GameState.gameStateOf(problem)

        val graph = ShortestPathUsingDijkstra
            .analyze(gameState)(RobotId(0), GameState.gameStateOf(Point(0, 0)))
            .getPath(
                Node(Point(0, 0), false),
                Node(Point(1, 2), false)
            )

        Assertions.assertEquals(graph.length, 3)
    }
}
