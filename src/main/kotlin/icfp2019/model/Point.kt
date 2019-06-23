package icfp2019.model

data class Point(val x: Int, val y: Int) {
    companion object {
        fun origin(): Point {
            return Point(0, 0)
        }
    }

    fun isNeighbor(otherPoint: Point): Boolean = when (otherPoint) {
        left() -> true
        right() -> true
        up() -> true
        down() -> true
        else -> false
    }

    fun actionToGetToNeighbor(neighbor: Point): Action = when (neighbor) {
        left() -> Action.MoveLeft
        right() -> Action.MoveRight
        up() -> Action.MoveUp
        down() -> Action.MoveDown
        else -> throw Exception("neighbor was not really a neighbor")
    }

    fun up(): Point {
        return copy(y = y + 1)
    }

    fun down(): Point {
        return copy(y = y - 1)
    }

    fun left(): Point {
        return copy(x = x - 1)
    }

    fun right(): Point {
        return copy(x = x + 1)
    }

    fun neighbors(): List<Point> {
        return listOf(up(), down(), left(), right())
    }
}
