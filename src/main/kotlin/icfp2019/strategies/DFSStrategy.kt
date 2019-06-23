package icfp2019.strategies

import icfp2019.analyzers.GraphAnalyzer
import icfp2019.analyzers.ShortestPathUsingFloydWarshall
import icfp2019.core.Strategy
import icfp2019.model.*
import org.jgrapht.Graph
import org.jgrapht.GraphPath
import org.jgrapht.graph.AsSubgraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.DepthFirstIterator
import org.jgrapht.traverse.GraphIterator

// Move to an open space and push moves onto a stack, if no moves available then backtrack using the stack
object DFSStrategy : Strategy {
    override fun compute(initialState: GameState): (robotId: RobotId, state: GameState) -> Action {
        return { robotId, gameState ->
            val graph: Graph<Node, DefaultEdge> = GraphAnalyzer.analyze(initialState).invoke(robotId, gameState)

            val currentPoint = gameState.robotState.values.first().currentPosition
            val currentNode = gameState.get(currentPoint)

            val unwrappedGraph =
                AsSubgraph(graph, graph.vertexSet().filter { it.isWrapped.not() }.plus(currentNode).toSet())

            val it: GraphIterator<Node, DefaultEdge> = DepthFirstIterator(unwrappedGraph)

            val neighbors = currentNode.point.neighbors().map { gameState.get(it) }
            if (neighbors.any {
                    it.isWrapped.not() && it.isObstacle.not()
                }) {
                val neighbor = it.next().point
                currentPoint.actionToGetToNeighbor(neighbor)
            } else {
                val analyze = ShortestPathUsingFloydWarshall.analyze(initialState)
                val shortestPathAlgorithm = analyze(robotId, gameState)

                val pathToClosestNode: GraphPath<Node, DefaultEdge> = unwrappedGraph.vertexSet()
                    .filter { it.point != currentNode.point }
                    .filter { it.isWrapped.not() }
                    .map { shortestPathAlgorithm.getPath(currentNode, it) }
                    .minBy { it.length }!!

                // pathToClosestNode.vertexList[0] is `currentNode`
                val nextNode = pathToClosestNode.vertexList[1]
                currentPoint.actionToGetToNeighbor(nextNode.point)
            }
        }
    }
}
