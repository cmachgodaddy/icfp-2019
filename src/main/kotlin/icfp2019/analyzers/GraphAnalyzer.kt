package icfp2019.analyzers

import icfp2019.model.GameState
import icfp2019.model.Node
import icfp2019.core.Analyzer
import icfp2019.model.RobotId
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph

object GraphAnalyzer : Analyzer<Graph<Node, DefaultEdge>> {
    override fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> Graph<Node, DefaultEdge> {

        val simpleGraph = SimpleGraph<Node, DefaultEdge>(DefaultEdge::class.java)
        val allCells = initialState.cells.flatten().filter { !it.isObstacle }

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

        return { _, _ -> simpleGraph }
    }
}
