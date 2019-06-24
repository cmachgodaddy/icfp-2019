package icfp2019.analyzers

import icfp2019.model.GameState
import icfp2019.core.Analyzer
import icfp2019.model.RobotId
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree
import org.jgrapht.graph.DefaultEdge

object MSTAnalyzer : Analyzer<SpanningTreeAlgorithm.SpanningTree<DefaultEdge>> {
    override fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> SpanningTreeAlgorithm.SpanningTree<DefaultEdge> {
        val completeGraph = BoardCellsGraphAnalyzer.analyze(initialState)
        return { robotId, state ->
            val graph = completeGraph(robotId, state)
            KruskalMinimumSpanningTree(graph).spanningTree
        }
    }
}
