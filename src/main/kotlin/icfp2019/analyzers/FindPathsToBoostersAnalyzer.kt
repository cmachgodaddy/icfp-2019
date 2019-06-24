package icfp2019.analyzers

import icfp2019.core.Analyzer
import icfp2019.model.*
import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge

object FindPathsToBoostersAnalyzer : Analyzer<(requestedBooster: Booster) -> List<GraphPath<BoardCell, DefaultEdge>>> {
    override fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> ((requestedBooster: Booster) -> List<GraphPath<BoardCell, DefaultEdge>>) {
        // First find the boosters and compute the paths
        val boosterPoints = initialState.boardState().allStates()
            .filter { it.hasBooster }
            .groupBy { it.booster!! }
            .mapValues { it.value.map { it.point } }

        val completeGraph = BoardCellsGraphAnalyzer.analyze(initialState)
        // completeGraph() doesn't actually use robotId or the state and simply returns the graph
        return { robotId, state ->
            val graph = completeGraph(robotId, initialState)
            val algorithm = DijkstraShortestPath(graph);

            { booster ->
                // Filter used boosters
                val robotNode = initialState.get(state.robot(robotId).currentPosition)
                boosterPoints.getOrDefault(booster, listOf())
                    .map { state.get(it) }
                    .map { algorithm.getPath(robotNode, it) }
            }
        }
    }
}
