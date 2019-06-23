package icfp2019

import com.google.common.base.CharMatcher
import com.google.common.base.Splitter
import com.google.common.collect.Range
import com.google.common.collect.TreeRangeSet
import icfp2019.model.*
import org.pcollections.TreePVector

class Splitters {
    companion object {
        val PAREN_MATCHER = CharMatcher.anyOf("()")!!
        val COMMA_SPLITTER = Splitter.on(',').omitEmptyStrings()!!
        val HASH_SPLITTER = Splitter.on('#')!!
        val SEMI_SPLITTER = Splitter.on(';').omitEmptyStrings()!!
    }
}

fun parsePoint(mapEdges: String): Point {
    return parseEdges(mapEdges).first()
}

fun parseEdges(mapEdges: String): List<Point> {
    // split a string like:
    //      (1,2),(3,4),(5,6)
    // in to:
    //      (1  2)  (3  4)  (5  6)
    // then read 2 at a time, trimming the parens
    return Splitters.COMMA_SPLITTER.split(mapEdges)
        .map { Splitters.PAREN_MATCHER.trimFrom(it) }
        .windowed(2, step = 2)
        .map { (first, second) -> Point(Integer.parseInt(first), Integer.parseInt(second)) }
}

fun parseObstacles(obstacles: String): List<List<Point>> {
    return Splitters.SEMI_SPLITTER.split(obstacles)
        .map { parseEdges(it) }
}

fun parseDesc(problem: String): Problem {
    val (mapEdges, startPosition, obstacles, boosters) = Splitters.HASH_SPLITTER.splitToList(problem)
    val startPoint = parsePoint(startPosition)
    val vertices = parseEdges(mapEdges)
    val obstacleEdges = parseObstacles(obstacles)
    val parsedBoosters = parseBoosters(boosters)

    val maxY = vertices.maxBy { it.y }?.y ?: throw IllegalArgumentException("No Vertices")
    val maxX = vertices.maxBy { it.x }?.x ?: throw IllegalArgumentException("No Vertices")

    val xArrayIndices = 0.until(maxX)
    val yArrayIndices = 0.until(maxY)

    // Initialize grid with each cell starting out as an obstacle
    val grid: Array<Array<Node>> =
        xArrayIndices.map { x ->
            yArrayIndices.map { y ->
                Node(Point(x, y), isObstacle = false)
            }.toTypedArray()
        }.toTypedArray()

    // Build a mapping of all the edges per column along the x-axis
    // Edges are represented as a Closed range between sorted why coordinates in the column
    val xEdgeGroups = (vertices + obstacleEdges.flatten()).groupBy { it.x }.mapValues { entry ->
        val set: TreeRangeSet<Int> = TreeRangeSet.create()
        val points: List<Point> = entry.value
        val sortedBy = points.map { it.y }.sortedBy { it }
        sortedBy.windowed(2, step = 2).forEach {
            val (y1, y2) = it
            set.add(Range.closed(y1, y2))
        }

        set
    }

    // this is the actual fill method for the map
    // This walks along the grid, one Y row at a time
    // Then walks the X axis for that row and for every edges crossed
    // it flips its inObstacle state
    yArrayIndices.forEach { y ->
        var inObstacle = true
        val currentYEdge = Range.closed(y, y + 1)

        xArrayIndices.forEach { x ->
            val yEdges = xEdgeGroups[x]

            if (yEdges?.encloses(currentYEdge) == true) {
                inObstacle = inObstacle.not()
            }

            val column = grid[x]
            column[y] = column[y].copy(isObstacle = inObstacle)
        }
    }

    parsedBoosters.forEach {
        grid[it.location.x][it.location.y] = grid[it.location.x][it.location.y].copy(booster = it.booster)
    }

    // Read lines
    /*
    1. Read lines
    2. Parse map
    Grammar:
      x,y: Nat
                point ::= (x,y)
                  map ::= repSep(point,”,”)
          BoosterCode ::= B|F|L|X
      boosterLocation ::= BoosterCode point
            obstacles ::= repSep(map,”; ”)
             boosters ::= repSep(boosterLocation,”; ”)
                 task ::= map # point # obstacles # boosters
     */
    return Problem(
        MapSize(maxX, maxY),
        startPoint,
        map = TreePVector.from(grid.map { TreePVector.from(it.toList()) })
    )
}

data class ParsedBooster(val booster: Booster, val location: Point)

fun parseBoosters(boosters: String): List<ParsedBooster> {
    return Splitters.SEMI_SPLITTER
        .split(boosters)
        .map {
            ParsedBooster(Booster.fromChar(it[0]), parsePoint(it.substring(1)))
        }
}
