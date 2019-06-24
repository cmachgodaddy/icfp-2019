package icfp2019.analyzers

import icfp2019.core.Analyzer
import icfp2019.model.BoardCell
import icfp2019.model.GameState
import icfp2019.model.RobotId
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths
import org.jgrapht.graph.DefaultEdge

object ShortestPathUsingFloydWarshall : Analyzer<ShortestPathAlgorithm<BoardCell, DefaultEdge>> {
    override fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> ShortestPathAlgorithm<BoardCell, DefaultEdge> {
        val completeGraph = BoardCellsGraphAnalyzer.analyze(initialState)
        return { id, graphState ->
            val graph = completeGraph(id, graphState)
            FloydWarshallShortestPaths(graph)
        }
    }
}
