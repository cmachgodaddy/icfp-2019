package icfp2019.analyzers

import icfp2019.model.GameState
import icfp2019.model.Point

import icfp2019.model.RobotId

import icfp2019.toProblem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ShortestPathsUsingFloydWarshallTest {

    @Test
    fun testShortestDistanceToPoint() {
        val map3x3 = """
            ...
            ...
            ...
        """.toProblem()

        val gameState = GameState(map3x3)

        val graph = ShortestPathUsingFloydWarshall
            .analyze(gameState)(RobotId.first, gameState)
            .getPath(
                gameState.get(Point.origin()),
                gameState.get(Point(1, 2))
            )

        Assertions.assertEquals(listOf(
            Point(0, 0),
            Point(0, 1),
            Point(0, 2),
            Point(1, 2)
        ),
            graph.vertexList.map { it.point }
        )
    }
}
