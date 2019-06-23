package icfp2019.analyzers

import icfp2019.model.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcollections.TreePVector

class GetNumberOfWrappedOrNotTests {
    @Test
    fun testSimple() {
        val gameState = GameState.gameStateOf(
            TreePVector.from(
                listOf(
                    TreePVector.from(
                        listOf(
                            Node(Point(0, 0), false, true),
                            Node(Point(0, 1), false)
                        )
                    ),
                    TreePVector.from(
                        listOf(
                            Node(Point(1, 0), false, true),
                            Node(Point(1, 1), false)
                        )
                    ),
                    TreePVector.from(
                        listOf(
                            Node(Point(2, 0), false),
                            Node(Point(2, 1), false)
                        )
                    )
                )
            ), MapSize(3, 2), Point(0, 0))

        val columns = gameState.cells
        Assertions.assertEquals(3, columns.size)
        Assertions.assertEquals(2, columns[0].size)
        Assertions.assertEquals(2, columns[1].size)
        Assertions.assertEquals(2, columns[2].size)

        val results = GetNumberOfWrappedOrNot.analyze(gameState)(RobotId(0), gameState)
        Assertions.assertEquals(2, results.wrapped)
        Assertions.assertEquals(4, results.unwrapped)
    }
}
