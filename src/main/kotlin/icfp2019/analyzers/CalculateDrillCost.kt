package icfp2019.analyzers

import icfp2019.Direction
import icfp2019.core.Analyzer
import icfp2019.model.DrillState
import icfp2019.model.GameState
import icfp2019.model.Node
import icfp2019.model.RobotId
import org.pcollections.PVector

object CalculateDrillCost : Analyzer<List<List<DrillState>>> {
    override fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> List<List<DrillState>> {

        val currentGrid = buildDrillRequiredFromEachNode(initialState.cells)
        return { _, state ->
            state.robotState.values.map { currentState ->
                currentGrid[currentState.currentPosition.x][currentState.currentPosition.y]
            }
        }
    }

    private fun buildDrillRequiredFromEachNode(currentGrid: PVector<PVector<Node>>): List<List<List<DrillState>>> {

        val xMax = currentGrid.size
        val yMax = currentGrid[0].size

        return currentGrid.mapIndexed { x, row ->
            row.mapIndexed { y, _ ->
                if (!currentGrid[x][y].isObstacle) {
                    listOf(DrillState(Direction.E, -1))
                } else {
                    val rightDir =
                        (x until xMax).map {
                            currentGrid[it][y]
                        }
                            .takeWhile { it.isObstacle }
                            .count()
                    DrillState(Direction.R, rightDir)

                    val leftDir = (x downTo 0).map {
                        currentGrid[it][y]
                    }
                        .takeWhile { it.isObstacle }
                        .count()
                    DrillState(Direction.L, leftDir)

                    val upDir = (y until yMax).map {
                        currentGrid[x][it]
                    }
                        .takeWhile { it.isObstacle }
                        .count()
                    DrillState(Direction.U, upDir)

                    val downDir = (y downTo 0).map {
                        currentGrid[x][it]
                    }
                        .takeWhile { it.isObstacle }
                        .count()
                    DrillState(Direction.D, downDir)

                    listOf(
                        DrillState(Direction.R, rightDir),
                        DrillState(Direction.L, leftDir),
                        DrillState(Direction.U, upDir),
                        DrillState(Direction.D, downDir)
                    )
                }
            }
        }
    }
}
