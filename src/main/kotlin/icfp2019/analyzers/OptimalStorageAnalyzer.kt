package icfp2019.analyzers

import icfp2019.core.Analyzer
import icfp2019.Cache
import icfp2019.model.Booster
import icfp2019.model.Cell
import icfp2019.model.GameState
import icfp2019.model.RobotId

object OptimalStorageAnalyzer : Analyzer<Array<Short>> {

    private val cache = Cache.forGameState { map ->
        val cells = Array<Short>(map.mapSize.x * map.mapSize.y) { 0 }
        for (column in map.cells) {
            for (cell in column) {
                val offset = cell.point.x * map.mapSize.y + cell.point.y
                cells[offset] = when {
                    cell.isObstacle -> Cell.OBSTACLE
                    cell.booster == Booster.ExtraArm -> Cell.BOOST_EXT
                    cell.booster == Booster.Drill -> Cell.BOOST_DRILL
                    cell.booster == Booster.FastWheels -> Cell.BOOST_FAST
                    cell.booster == Booster.Teleporter -> Cell.BOOST_TELEPORT
                    cell.booster == Booster.CloneToken -> Cell.BOOST_CLONE
                    else -> Cell.ZERO
                }
            }
        }

        cells
    }

    override fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> Array<Short> {
        return { _, state -> cache(state) }
    }
}
