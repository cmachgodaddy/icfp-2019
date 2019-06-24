package icfp2019.analyzers

import icfp2019.model.Booster
import icfp2019.model.GameState
import icfp2019.model.RobotId
import icfp2019.toProblem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FindPathsToBoostersTests {
    @Test
    fun test1() {
        val problem = """
        ...XX
        ....b
        .@.XX
    """.toProblem()

        val gs = GameState(problem)
        val analyzer = FindPathsToBoostersAnalyzer.analyze(gs)
        val r = analyzer(RobotId.first, gs)(Booster.ExtraArm)
        Assertions.assertEquals(4, r[0].length)
    }

    @Test
    fun test2() {
        val problem = """
        ...XX
        b...b
        .@.XX
    """.toProblem()

        val gs = GameState(problem)
        val analyzer = FindPathsToBoostersAnalyzer.analyze(gs)
        val r = analyzer(RobotId.first, gs)(Booster.ExtraArm)
        Assertions.assertEquals(2, r[0].length)
    }
}
