package icfp2019.model

import org.pcollections.PVector

data class GameBoard(
    val cells: PVector<PVector<Node>>,
    val width: Int,
    val height: Int
) {
    constructor(problem: Problem) : this(problem.map, problem.size.x, problem.size.y)

    fun isInBoard(point: Point): Boolean {
        return (point.x in 0 until width && point.y in 0 until height)
    }

    fun get(point: Point): Node {
        if (!isInBoard(point)) {
            throw ArrayIndexOutOfBoundsException("Access out of game board")
        }
        return cells[point.x][point.y]
    }

    fun set(point: Point, value: Node): GameBoard {
        if (!isInBoard(point)) {
            throw ArrayIndexOutOfBoundsException("Access out of game board")
        }
        val newCells = cells.with(point.x, cells[point.x].with(point.y, value))
        return GameBoard(newCells, width, height)
    }
}
