package icfp2019.core

import icfp2019.model.Action
import icfp2019.model.GameState
import icfp2019.model.RobotId

interface Strategy {
    fun compute(initialState: GameState): (robotId: RobotId, state: GameState) -> Action
}
