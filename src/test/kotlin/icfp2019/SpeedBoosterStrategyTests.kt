package icfp2019

import icfp2019.model.Action
import icfp2019.model.GameState
import icfp2019.model.RobotId
import icfp2019.strategies.SpeedBoosterStrategy
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SpeedBoosterStrategyTests {
    @Test
    fun test1() {
        val problem = """
        ...XX
        w@..f
        .w.XX
    """.toProblem()
        val gs = GameState(problem)
        val strategy = SpeedBoosterStrategy.compute(gs)
        val r = strategy(RobotId.first, gs)
        Assertions.assertEquals(Action.MoveRight, r)
    }

    @Test
    fun test2() {
        val problem = """
        f..XX
        w@...
        .w.XX
    """.toProblem()
        val gs = GameState(problem)
        val strategy = SpeedBoosterStrategy.compute(gs)
        val r = strategy(RobotId.first, gs)
        Assertions.assertTrue(r == Action.MoveUp || r == Action.MoveLeft)
    }
}