package icfp2019

import icfp2019.model.Booster
import icfp2019.model.Node
import icfp2019.model.Point
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ParserTests {
    @Test
    fun testParser() {
        val result: List<Point> = parseEdges("(1,2),(3,4)")
        Assertions.assertEquals(result, listOf(Point(1, 2), Point(3, 4)))
    }

    @Test
    fun simpleBoard() {
        val problem1Input = "(0,0),(6,0),(6,1),(8,1),(8,2),(6,2),(6,3),(0,3)#(0,0)##"

        val problem = parseDesc(problem1Input, "Test")
        /* 0 1 2 3 4 5 6 7
         2 . . . . . . X X
         1 . . . . . . . .
         0 . . . . . . X X
         */

        val obstacle = Node(Point(0, 0), isObstacle = true)
        val free = Node(Point(0, 0), isObstacle = false)

        val expectedMap = listOf(
            listOf(
                free.copy(point = Point(0, 0)),
                free.copy(point = Point(0, 1)),
                free.copy(point = Point(0, 2))
            ),
            listOf(
                free.copy(point = Point(1, 0)),
                free.copy(point = Point(1, 1)),
                free.copy(point = Point(1, 2))
            ),
            listOf(
                free.copy(point = Point(2, 0)),
                free.copy(point = Point(2, 1)),
                free.copy(point = Point(2, 2))
            ),
            listOf(
                free.copy(point = Point(3, 0)),
                free.copy(point = Point(3, 1)),
                free.copy(point = Point(3, 2))
            ),
            listOf(
                free.copy(point = Point(4, 0)),
                free.copy(point = Point(4, 1)),
                free.copy(point = Point(4, 2))
            ),
            listOf(
                free.copy(point = Point(5, 0)),
                free.copy(point = Point(5, 1)),
                free.copy(point = Point(5, 2))
            ),
            listOf(
                obstacle.copy(point = Point(6, 0)),
                free.copy(point = Point(6, 1)),
                obstacle.copy(point = Point(6, 2))
            ),
            listOf(
                obstacle.copy(point = Point(7, 0)),
                free.copy(point = Point(7, 1)),
                obstacle.copy(point = Point(7, 2))
            )
        )

        printBoard(problem)

        expectedMap.forEachIndexed { x, column ->
            Assertions.assertEquals(column, problem.map[x].toList(), "X($x) doesn't  match")
        }
    }

    @Test
    fun printProblem() {
        val problemInput = loadProblem(86)
        printBoard(parseDesc(problemInput, "Test"))
    }

    @Test
    fun testProblem3() {
        val problem3Input = loadProblem(3)

        val p = parseDesc(problem3Input, "Test")

        val expectedBoard = "X X . . . . . . X X X X X X X X X X X X X X X X X X X\n" +
                "X X . . . . . . X X X X X X X X X X X X X X X X X X X\n" +
                "X X . . . . . . X X X X X X X X X X X X X X X X X X X\n" +
                "X X . . . . . . X X X X X X X X X X X X X X X X X X X\n" +
                "X X . . . . . . X X X X X X X X . . X X X X X X X X X\n" +
                "X X . . o . . . X X X . . . . . . . X X X X X X X X X\n" +
                "X X . . o . . . X X X . . . . . . . X X X X X X X X X\n" +
                "X X . . . . . . X X X X X X X X . . X X X X X X X X X\n" +
                "X X . . . . . . X . . . X X X X . . X X X X X X X X X\n" +
                "X X . . . . . . X . . . X X X X . . X X X X X X X X X\n" +
                "X X X . . . . . . . . . X X X . . . . . . . . . X X X\n" +
                "X X X . . . . . . . . . X X X . o . . . . . . . X X X\n" +
                "X X X . . . . . . . . . X . . . . . . X X X X X X X X\n" +
                "X X X X X X X X X . . . . . . . . . . X X X X X X X X\n" +
                "@ . . . . . . . . . . . . . . X . . . . . . X X X X X\n" +
                "X X X X X X X X X . . . . X X X . o . . . . X X X X X\n" +
                "X X X X X X X X X X X X X X X X . . . . . . X X X X X\n" +
                "X X X X X X X X X X X X . . . X . . . o . . X X X X X\n" +
                "X X X X X X X X X X . . . . . X . . . . . . X X X X X\n" +
                "X X X X X X X X X X . . . . . X . . . . . . X X X X X\n" +
                "X X X X X X X X X X . . X . . . . . . . . . X X X X X\n" +
                "X X X X X X X X X X X X X . . . . . . . . . X X X X X\n" +
                "X X X X X X X X X X X X X . . . X X . . . . . . . . .\n" +
                "X X X X X X X X X X X X X X . . X X . . . X . X . . .\n" +
                "X X X X X X X X X X X X X X . . X X . . X X X X X X .\n" +
                "X X X X X X X X X X X X X X . . X X . . X X X X X X .\n" +
                "X X X X X X X X X X X X X X . . X X . . X X X X X X .\n" +
                "X X X X X X X X X X X X X X . . X X . . X X X X X X .\n" +
                "X X X X X X X X X X X X X X . . X X . . X X X X X X .\n" +
                "X X X X X X X X X X X X X X X X X X . . X X X X X X .\n" +
                "X X X X X X X X X X X X X X X X X X . . . . . . X . .\n" +
                "X X X X X X X X X X X X X X X X X X . . . X . . X . .\n" +
                "X X X X X X X X X X X X X X X X X X . X X X X X X . .\n" +
                "X X X X X X X X X X X X X X X X X X . . X . X . X . .\n" +
                "X X X X X X X X X X X X X X X X X X . X X . . . . . .\n" +
                "X X X X X X X X X X X X X X X X X X . . X . . . . . .\n" +
                "X X X X X X X X X X X X X X X X X X . . . . . . . . ."

        Assertions.assertEquals(expectedBoard, boardString(p))
    }

    @Test
    fun testBoosterParser() {
        val boosters = "X(16,25);L(19,19);F(4,30);F(17,21);B(4,31)"
        val actual = parseBoosters(boosters)
        val expected = listOf(
            ParsedBooster(Booster.CloningLocation, Point(16, 25)),
            ParsedBooster(Booster.Drill, Point(19, 19)),
            ParsedBooster(Booster.FastWheels, Point(4, 30)),
            ParsedBooster(Booster.FastWheels, Point(17, 21)),
            ParsedBooster(Booster.ExtraArm, Point(4, 31))
        )
        Assertions.assertEquals(actual, expected)
    }
}
