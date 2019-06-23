package icfp2019.analyzers

import icfp2019.model.GameState
import icfp2019.model.Point

import icfp2019.model.RobotId

import icfp2019.printBoard

import icfp2019.toProblem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ShortestPathUsingDijkstraTest {

    @Test
    fun testShortestDistanceToPoint() {
        val map3x2 = """
            ...
            ...
        """.toProblem()

        val gameState = GameState(map3x2)

        printBoard(map3x2)

        val graph = ShortestPathUsingDijkstra
            .analyze(gameState)(RobotId.first, gameState)
            .getPath(
                gameState.get(Point.origin()),
                gameState.get(Point(2, 1))
            )

        Assertions.assertEquals(listOf(
            Point(0, 0),
            Point(0, 1),
            Point(1, 1),
            Point(2, 1)
        ),
            graph.vertexList.map { it.point }
        )
    }
}
