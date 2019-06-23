package icfp2019.model

import icfp2019.Direction

fun buildDrillRequiredFromEachNode(problem: Problem): Array<Array<Array<DrillState>>> {

    // filter { !it.isObstacle }
    val currentGrid = problem.map
    return currentGrid.mapIndexed { x, row ->
        row.mapIndexed { y, _ ->
            if (!currentGrid[x][y].isObstacle) {
                arrayOf(DrillState(Direction.E, -1)) // -1
            } else {
                val rightDir =
                    (x until problem.size.x).map {
                        currentGrid[it][y]
                    }
                        .takeWhile { it.isObstacle }
                        .count()

                val leftDir = (x downTo 0).map {
                    currentGrid[it][y]
                }
                    .takeWhile { it.isObstacle }
                    .count()

                val upDir = (y until problem.size.y).map {
                    currentGrid[x][it]
                }
                    .takeWhile { it.isObstacle }
                    .count()

                val downDir = (y downTo 0).map {
                    currentGrid[x][it]
                }
                    .takeWhile { it.isObstacle }
                    .count()

                arrayOf(
                    DrillState(Direction.R, rightDir),
                    DrillState(Direction.L, leftDir),
                    DrillState(Direction.U, upDir),
                    DrillState(Direction.D, downDir)
                    )
            }
        }.toTypedArray()
    }.toTypedArray()
}
