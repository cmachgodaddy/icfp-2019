package icfp2019.strategies

import icfp2019.analyzers.BoardCellsGraphAnalyzer
import icfp2019.analyzers.ShortestPathUsingDijkstra
import icfp2019.core.Strategy
import icfp2019.model.*
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.BreadthFirstIterator
import org.jgrapht.traverse.GraphIterator

fun <T> Sequence<T>.sample(count: Int): Sequence<T> = toMutableList()
    .shuffled()
    .take(count)
    .asSequence()

object BFSStrategy : Strategy {
    override fun compute(initialState: GameState): (robotId: RobotId, state: GameState) -> Action {
        val graphBuilder = BoardCellsGraphAnalyzer.analyze(initialState)
        return { robotId, gameState ->
            if (gameState.boostersAvailable(Booster.ExtraArm) > 0) {
                Action.AttachManipulator(gameState.robot(robotId).optimumManipulatorArmTarget())
                // } else if (gameState.boostersAvailable(Booster.FastWheels) > 0) {
                //    Action.AttachFastWheels
            } else {
                val graph = graphBuilder.invoke(robotId, gameState)
                val currentPoint = gameState.robot(robotId).currentPosition
                val currentNode = gameState.get(currentPoint)

                val neighbors = currentNode.point.neighbors()
                    .filter {
                        gameState.isInBoard(it) &&
                                gameState.nodeState(it).isWrapped.not() &&
                                gameState.get(it).isObstacle.not()
                    }

                if (neighbors.any()) {
//                    bfsIterator.next() // move past currentNode
//                    val neighbor = bfsIterator.next().point
                    currentPoint.actionToGetToNeighbor(neighbors.first())
                } else {
                    val bfsIterator: GraphIterator<BoardCell, DefaultEdge> = BreadthFirstIterator(graph, currentNode)

                    var seenUnwrapped = false
                    var node: BoardCell? = null
                    for (boardCell in bfsIterator) {
                        if (seenUnwrapped) break
                        seenUnwrapped = !gameState.nodeState(boardCell.point).isWrapped
                        node = boardCell
                    }

                    val analyze = ShortestPathUsingDijkstra.analyze(gameState)
                    val shortestPathAlgorithm = analyze(robotId, gameState)
                    val firstStep = shortestPathAlgorithm.getPath(currentNode, node).vertexList.drop(1).first()
                    currentPoint.actionToGetToNeighbor(firstStep.point)
                }
            }
        }
    }
}
