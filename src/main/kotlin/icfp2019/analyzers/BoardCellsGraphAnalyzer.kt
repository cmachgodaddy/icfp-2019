package icfp2019.analyzers

import icfp2019.Cache
import icfp2019.core.Analyzer
import icfp2019.model.BoardCell
import icfp2019.model.GameState
import icfp2019.model.RobotId
import icfp2019.model.allCells
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph

object BoardCellsGraphAnalyzer : Analyzer<Graph<BoardCell, DefaultEdge>> {
    private val cache = Cache.forBoard { initialState ->
        val simpleGraph = SimpleGraph<BoardCell, DefaultEdge>(DefaultEdge::class.java)
        val allCells = initialState.allCells().filter { !it.isObstacle }.toList()

        allCells.forEach {
            simpleGraph.addVertex(it)
        }

        allCells.forEach { n1 ->
            allCells.forEach { n2 ->
                if (n1.point.isNeighbor(n2.point)) {
                    simpleGraph.addEdge(n1, n2)
                }
            }
        }

        simpleGraph
    }

    override fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> Graph<BoardCell, DefaultEdge> {
        return { _, state -> cache(state.board()) }
    }
}
