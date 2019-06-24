package icfp2019.analyzers

import icfp2019.core.Analyzer
import icfp2019.model.Action
import icfp2019.model.GameState
import icfp2019.model.RobotId
import icfp2019.strategies.BFSStrategy

object BFSAnalyzer : Analyzer<Action> {
    override fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> Action {
        return { robotId, state -> BFSStrategy.compute(initialState).invoke(robotId, state) }
    }
}
