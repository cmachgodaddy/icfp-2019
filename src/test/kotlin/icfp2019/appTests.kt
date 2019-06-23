package icfp2019

import icfp2019.model.MapSize
import icfp2019.model.Node
import icfp2019.model.Point
import icfp2019.model.Problem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.pcollections.PVector
import org.pcollections.TreePVector

class appTests {
    @Test
    fun testObstacleMap() {

        val nodeMap = TreePVector.from(
            listOf<PVector<Node>>(
                TreePVector.from(
                    listOf(
                        Node(Point(0, 0), false),
                        Node(Point(0, 1), false),
                        Node(Point(0, 2), true),
                        Node(Point(0, 3), false)
                    )
                ),

                TreePVector.from(
                    listOf(
                        Node(Point(1, 0), true),
                        Node(Point(1, 1), false),
                        Node(Point(1, 2), true),
                        Node(Point(1, 3), false)
                    )
                )
            )
        )

        val problem = Problem(MapSize(2, 4), Point(0, 0), nodeMap)
        val obstacle = constructObstacleMap(problem)

        val expected = arrayOf(
            arrayOf(false, false, true, false),
            arrayOf(true, false, true, false)
        )

        Assertions.assertArrayEquals(obstacle, expected)
    }
}
