package icfp2019.analyzers

import icfp2019.model.GameState
import icfp2019.model.RobotId
import icfp2019.toProblem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GetNumberOfWrappedOrNotTests {
    @Test
    fun testSimple() {

        val map3x2 = """
            ...
            ww.
        """.toProblem()
        val gameState = GameState(map3x2)

        val columns = gameState.cells
        Assertions.assertEquals(3, columns.size)
        Assertions.assertEquals(2, columns[0].size)
        Assertions.assertEquals(2, columns[1].size)
        Assertions.assertEquals(2, columns[2].size)

        val results = GetNumberOfWrappedOrNot.analyze(gameState)(RobotId.first, gameState)
        Assertions.assertEquals(2, results.wrapped)
        Assertions.assertEquals(4, results.unwrapped)
    }
}
