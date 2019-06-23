package icfp2019

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TestUtilsKtTest {
    @Test
    fun `make sure x and y are parsed correctly`() {
        val problem = """
                        XXXXXXXX
                        X.@wwwwX
                        XXXXXXXX
                    """.toProblem()
        Assertions.assertTrue(
            problem.get(1, 1).isWrapped.not() &&
                    problem.get(1, 1).isObstacle.not()
        )

        Assertions.assertTrue(
            problem.get(2, 1).isWrapped.not() &&
                    problem.get(2, 1).isObstacle.not()
        )

        Assertions.assertTrue(
            problem.get(7, 2).isWrapped.not() &&
                    problem.get(7, 2).isObstacle
        )

        Assertions.assertTrue(
            problem.get(6, 1).isWrapped &&
                    problem.get(6, 1).isObstacle.not()
        )
    }
}