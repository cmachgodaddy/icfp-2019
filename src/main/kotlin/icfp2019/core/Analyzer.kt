package icfp2019.core

import icfp2019.model.GameState
import icfp2019.model.RobotId

interface Analyzer<T> {
    fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> T
}
