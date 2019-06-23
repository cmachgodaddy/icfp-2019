package icfp2019.analyzers

import icfp2019.core.Analyzer
import icfp2019.model.Action
import icfp2019.model.GameState
import icfp2019.model.RobotId
import icfp2019.strategies.DFSStrategy

object DFSAnalyzer : Analyzer<Iterable<Action>> {
    override fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> Iterable<Action> {
        return { robotId, state -> listOf(DFSStrategy.compute(initialState).invoke(robotId, state)) }
    }
}
