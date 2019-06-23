package icfp2019.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BitMaskTests {
    @Test
    fun testBitMask() {
        val cellValue: Short = 0

        val cellValue2 = Cell.setFlag(cellValue, Cell.OBSTACLE)
        Assertions.assertTrue(Cell.hasFlag(cellValue2, Cell.OBSTACLE))

        val cellValue3 = Cell.unsetFlag(cellValue2, Cell.OBSTACLE)
        Assertions.assertFalse(Cell.hasFlag(cellValue3, Cell.OBSTACLE))

        val cellValue4 = Cell.setFlags(cellValue3, Cell.OBSTACLE, Cell.BOOST_DRILL, Cell.SPAWN_POINT, Cell.WRAPPED)
        Assertions.assertTrue(Cell.hasFlag(cellValue4, Cell.OBSTACLE))
        Assertions.assertTrue(Cell.hasFlag(cellValue4, Cell.WRAPPED))
        Assertions.assertTrue(Cell.hasFlag(cellValue4, Cell.BOOST_DRILL))
        Assertions.assertTrue(Cell.hasFlag(cellValue4, Cell.SPAWN_POINT))
        Assertions.assertFalse(Cell.hasFlag(cellValue4, Cell.BOOST_EXT))
        Assertions.assertFalse(Cell.hasFlag(cellValue4, Cell.BOOST_TELEPORT))
        Assertions.assertFalse(Cell.hasFlag(cellValue4, Cell.BOOST_FAST))
        Assertions.assertFalse(Cell.hasFlag(cellValue4, Cell.BOOST_CLONE))
    }
}
