package icfp2019.model

import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

class Cell {
    companion object {
        const val ZERO: Short = 0 // This is NOT representing an empty cell, it means NO FLAGS, avoid a runtime cast

        // Values for cell properties
        const val OBSTACLE: Short = 1 // This cell is a wall/obstacle
        const val WRAPPED: Short = 2 // This cell has been 'wrapped' by the bot
        const val SPAWN_POINT: Short = 4 // This cell contains a 'spawn' object where cloning can occur
        const val BOOST_EXT: Short = 8 // This cell contains an unclaimed extension boost
        const val BOOST_FAST: Short = 16 // This cell contains an unclaimed fast wheels boost
        const val BOOST_DRILL: Short = 32 // This cell contains an unclaimed drill boost
        const val BOOST_TELEPORT: Short = 64 // This cell contains an unclaimed teleport boost
        const val BOOST_CLONE: Short = 128 // This cell contains a clone boost

        fun setFlag(value: Short, flag: Short): Short {
            return value or flag
        }

        fun setFlags(value: Short, vararg flags: Short): Short {
            return flags.foldRight(value, { flag, acc -> setFlag(acc, flag) })
        }

        fun unsetFlag(value: Short, flag: Short): Short {
            return value and flag.inv()
        }

        fun hasFlag(value: Short, flag: Short): Boolean {
            // an Undefined flag is a special case.
            if (value == ZERO || (value > ZERO && flag == ZERO)) return false

            return value and flag == flag
        }
    }
}
