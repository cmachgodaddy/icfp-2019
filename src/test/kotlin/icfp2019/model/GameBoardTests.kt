package icfp2019.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcollections.TreePVector

class GameBoardTests {
    @Test
    fun testGameBoardRowsSequence() {
        var g = GameBoard(
            TreePVector.from(
                listOf(
                    TreePVector.from(
                        listOf(
                            Node(Point(0, 0), false),
                            Node(Point(0, 1), false)
                        )
                    ),
                    TreePVector.from(
                        listOf(
                            Node(Point(1, 0), false),
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
            ), 3, 2
        )
        val columns = g.cells
        Assertions.assertEquals(3, columns.size)
        Assertions.assertEquals(2, columns[0].size)
        Assertions.assertEquals(2, columns[1].size)
        Assertions.assertEquals(2, columns[2].size)
        Assertions.assertEquals(g.get(Point(0, 0)),
            Node(Point(0, 0), false)
        )
        Assertions.assertEquals(g.get(Point(1, 0)),
            Node(Point(1, 0), false)
        )
        Assertions.assertEquals(g.get(Point(2, 0)),
            Node(Point(2, 0), false)
        )
        Assertions.assertEquals(Node(Point(0, 0), false), columns[0][0])
        Assertions.assertEquals(Node(Point(0, 1), false), columns[0][1])
        Assertions.assertEquals(Node(Point(1, 0), false), columns[1][0])
        Assertions.assertEquals(Node(Point(1, 1), false), columns[1][1])
        Assertions.assertEquals(Node(Point(2, 0), false), columns[2][0])
        Assertions.assertEquals(Node(Point(2, 1), false), columns[2][1])
    }
}
