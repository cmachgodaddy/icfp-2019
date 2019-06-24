package icfp2019.analyzers

import icfp2019.core.Analyzer
import icfp2019.model.Booster
import icfp2019.model.GameState
import icfp2019.model.Node
import icfp2019.model.RobotId
import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge

object FindPathsToBoostersAnalyzer : Analyzer<(requestedBooster: Booster) -> List<GraphPath<Node, DefaultEdge>>> {
    override fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> ((requestedBooster: Booster) -> List<GraphPath<Node, DefaultEdge>>) {
        // First find the boosters and compute the paths
        val boosters = initialState.cells.flatten().filter { it.isBooster() }
        val completeGraph = GraphAnalyzer.analyze(initialState)
        // completeGraph() doesn't actually use robotId or the state and simply returns the graph
        val graph = completeGraph(RobotId(0), initialState)
        val algorithm = DijkstraShortestPath(graph)
        return { robotId, state ->
            { booster ->
                // Filter used boosters
                val robotNode = initialState.get(state.robot(robotId).currentPosition)
                boosters
                    .filter { it.hasBooster(booster) }
                    .map { algorithm.getPath(robotNode, it) }
            }
        }
    }
}
