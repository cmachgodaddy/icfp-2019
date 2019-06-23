package icfp2019.analyzers

import icfp2019.core.Analyzer
import icfp2019.model.GameState
import icfp2019.model.Node
import icfp2019.model.RobotId
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge

object ShortestPathUsingDijkstra : Analyzer<ShortestPathAlgorithm<Node, DefaultEdge>> {
    override fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> ShortestPathAlgorithm<Node, DefaultEdge> {
        val completeGraph = GraphAnalyzer.analyze(initialState)
        return { robotId, graphState ->
            val graph = completeGraph(robotId, graphState)
            DijkstraShortestPath(graph)
        }
    }
}