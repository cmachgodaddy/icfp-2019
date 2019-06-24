package icfp2019.analyzers

import icfp2019.loadProblem
import icfp2019.model.GameState
import icfp2019.model.RobotId
import icfp2019.parseDesc
import kMetis
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class KMetisTest {
    @Test
    fun kMetis() {
        val problemInput = loadProblem(3)
        val desc = parseDesc(problemInput, "Test")
        val gameState = GameState(desc)
        val boardCells = BoardCellsGraphAnalyzer.analyze(gameState)(RobotId.first, gameState)
        val split = kMetis(boardCells, setOf(), 4)
        split.forEachIndexed { idx, x ->
            println("$idx: $x")
        }
        println(split)
    }
}
