package icfp2019

import icfp2019.model.Node
import org.pcollections.PVector

enum class Direction {
    U, D, L, R, E
}

fun applyLongestStraightPathAlgorithm(map: PVector<PVector<Node>>): List<List<Pair<Direction, Int>>> {
    val maxX = map.size
    val maxY = map[0].size
    val ret = map.mapIndexed { x, subArray ->
        subArray.mapIndexed { y, node ->
            if (node.isObstacle) {
                Direction.E to -1
            } else {
                val leftSum = (x - 1 downTo 0)
                    .map { map[it][y] }
                    .takeWhile { !it.isObstacle }
                    .count()
                val rightSum = (x + 1 until maxX)
                    .map { map[it][y] }
                    .takeWhile { !it.isObstacle }
                    .count()
                val upSum = (y + 1 until maxY)
                    .map { map[x][it] }
                    .takeWhile { !it.isObstacle }
                    .count()
                val downSum = (y - 1 downTo 0)
                    .map { map[x][it] }
                    .takeWhile { !it.isObstacle }
                    .count()
                listOf(
                    Direction.L to leftSum,
                    Direction.R to rightSum,
                    Direction.U to upSum,
                    Direction.D to downSum
                ).maxBy { it.second }!!
            }
        }
    }
    return ret
}
