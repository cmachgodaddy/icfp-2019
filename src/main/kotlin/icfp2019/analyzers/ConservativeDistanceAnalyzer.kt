package icfp2019.analyzers

import icfp2019.core.Analyzer
import icfp2019.core.DistanceEstimate
import icfp2019.model.*
import org.jgrapht.Graph
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree
import org.jgrapht.graph.AsSubgraph
import org.jgrapht.graph.DefaultEdge

data class ConservativeDistance(val estimate: DistanceEstimate, val pathNodes: Set<Point>)
object ConservativeDistanceAnalyzer : Analyzer<(position: Point) -> ConservativeDistance> {
    override fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> (position: Point) -> ConservativeDistance {
        val graphAnalyzer = BoardCellsGraphAnalyzer.analyze(initialState)
        val shortestPathAnalyzer = ShortestPathUsingDijkstra.analyze(initialState)
        return { id, state ->
            val graph: Graph<BoardCell, DefaultEdge> = graphAnalyzer(id, state)
            val shortestPathAlgorithm = shortestPathAnalyzer(id, state)
            val unwrappedNodes = AsSubgraph(
                graph,
                state.boardState().allStates()
                    .filter { it.isWrapped.not() }
                    .map { state.get(it.point) }
                    .toSet()
            )
            val connectivityInspector = ConnectivityInspector(unwrappedNodes)
            val connectedGraphs = connectivityInspector.connectedSets();

            { point ->
                val node = initialState.get(point)
                val randomNodesFromEach = connectedGraphs.map { it.first() }.plus(node).toSet()

                val elements: List<BoardCell> = connectedGraphs.flatten().map { state.get(it.point) }
                val map = randomNodesFromEach.windowed(2, step = 1).map { (n1, n2) ->
                    shortestPathAlgorithm.getPath(state.get(n1.point), state.get(n2.point))
                }
                val nodes: Set<BoardCell> =
                    map.flatMap { it.vertexList }.plus(elements).toSet()

                val connectedUnwrappedNodes = AsSubgraph(graph, nodes)

                val spanningTree = PrimMinimumSpanningTree(connectedUnwrappedNodes).spanningTree
                ConservativeDistance(
                    DistanceEstimate(spanningTree.count()),
                    spanningTree.edges.flatMap {
                        listOf(
                            graph.getEdgeSource(it).point,
                            graph.getEdgeTarget(it).point
                        )
                    }.toSet()
                )
            }
        }
    }
}
