package icfp2019.analyzers

import icfp2019.core.Analyzer
import icfp2019.core.DistanceEstimate
import icfp2019.model.GameState
import icfp2019.model.Node
import icfp2019.model.Point
import icfp2019.model.RobotId
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree
import org.jgrapht.graph.AsSubgraph

data class ConservativeDistance(val estimate: DistanceEstimate, val pathNodes: Set<Node>)
object ConservativeDistanceAnalyzer : Analyzer<(position: Point) -> ConservativeDistance> {
    override fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> (position: Point) -> ConservativeDistance {
        val graphAnalyzer = GraphAnalyzer.analyze(initialState)
        val shortestPathAnalyzer = ShortestPathUsingDijkstra.analyze(initialState)
        return { id, state ->
            val graph = graphAnalyzer(id, state)
            val shortestPathAlgorithm = shortestPathAnalyzer(id, state)
            val unwrappedNodes = AsSubgraph(graph, graph.vertexSet().filter { it.isWrapped.not() }.toSet())
            val connectivityInspector = ConnectivityInspector(unwrappedNodes)
            val connectedGraphs = connectivityInspector.connectedSets();

            { point ->
                val node = initialState.get(point)
                val randomNodesFromEach: Set<Node> =
                    connectedGraphs.map { it.first() }
                        .plus(node)
                        .toSet()

                val nodes: Set<Node> = randomNodesFromEach.windowed(2, step = 1).map { (n1, n2) ->
                    shortestPathAlgorithm.getPath(n1, n2)
                }.flatMap { it.vertexList }.plus(connectedGraphs.flatten()).toSet()

                val connectedUnwrappedNodes = AsSubgraph(graph, nodes)

                val spanningTree = PrimMinimumSpanningTree(connectedUnwrappedNodes).spanningTree
                ConservativeDistance(
                    DistanceEstimate(spanningTree.count()),
                    spanningTree.edges.flatMap {
                        listOf(
                            graph.getEdgeSource(it),
                            graph.getEdgeTarget(it)
                        )
                    }.toSet()
                )
            }
        }
    }
}
