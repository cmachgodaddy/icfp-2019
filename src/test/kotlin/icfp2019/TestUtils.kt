package icfp2019

import com.google.common.base.CharMatcher
import com.google.common.base.Splitter
import icfp2019.model.*
import org.pcollections.PVector
import org.pcollections.TreePVector
import java.nio.file.Paths

fun loadProblem(problemNumber: Int): String {
    val path = Paths.get("problems/prob-${problemNumber.toString().padStart(3, padChar = '0')}.desc").toAbsolutePath()
    return path.toFile().readText()
}
typealias Cells = PVector<PVector<Node>>

fun boardString(problem: Problem, path: Set<Node> = setOf()): String =
    boardString(problem.map, problem.size, problem.startingPosition, path)

fun boardString(cells: Cells, size: MapSize, startingPosition: Point, path: Set<Node> = setOf()): String {
    val lines = mutableListOf<String>()
    for (y in (size.y - 1) downTo 0) {
        val row = (0 until size.x).map { x ->
            val node = cells[x][y]
            when {
                node.hasTeleporterPlanted -> '*'
                node.isWrapped -> 'w'
                startingPosition == Point(x, y) -> '@'
                node.isObstacle -> 'X'
                node in path -> '|'
                node.booster != null -> 'o'
                else -> '.'
            }
        }.joinToString(separator = " ")
        lines.add(row)
    }
    return lines.joinToString(separator = "\n")
}

fun printBoard(p: Problem, path: Set<Node> = setOf()) {
    println("${p.size}")
    print(boardString(p.map, p.size, p.startingPosition, path))
    println()
}

fun printBoard(state: GameState, path: Set<Node> = setOf()) {
    println("${state.mapSize}")
    print(boardString(state.cells, state.mapSize, state.startingPoint, path))
    println()
}

fun String.toProblem(): Problem {
    return parseTestMap(this)
}

fun parseTestMap(map: String): Problem {
    val mapLineSplitter = Splitter.on(CharMatcher.anyOf("\r\n")).omitEmptyStrings()
    val lines = mapLineSplitter.splitToList(map)
        .map { CharMatcher.whitespace().removeFrom(it) }
        .filter { it.isBlank().not() }
        .reversed()
    val height = lines.size
    val width = lines[0].length
    if (lines.any { it.length != width }) throw IllegalArgumentException("Inconsistent map line lengths")
    val startPoint =
        (0 until width).map { x ->
            (0 until height).map { y ->
                if (lines[y][x] == '@') Point(x, y)
                else null
            }
        }.flatten().find { it != null } ?: Point.origin()
    return Problem("Test", MapSize(width, height), startPoint, TreePVector.from((0 until width).map { x ->
        TreePVector.from((0 until height).map { y ->
            val point = Point(x, y)
            when (val char = lines[y][x]) {
                'X' -> Node(point, isObstacle = true)
                'w' -> Node(point, isObstacle = false, isWrapped = true)
                '.' -> Node(point, isObstacle = false)
                '@' -> Node(point, isObstacle = false)
                '*' -> Node(point, isObstacle = false, hasTeleporterPlanted = true, isWrapped = true)
                in Booster.parseChars -> Node(point, isObstacle = false, booster = Booster.fromChar(char))
                else -> throw IllegalArgumentException("Unknown Char '$char'")
            }
        })
    }))
}
