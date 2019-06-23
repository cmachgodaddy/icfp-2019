package icfp2019.core

import icfp2019.model.Node
import icfp2019.model.Point
import org.pcollections.PVector

typealias MapCells = PVector<PVector<Node>>

fun MapCells.get(point: Point): Node {
    try {
        return this[point.x][point.y]
    } catch (e: Exception) {
        println("Point $point")
        throw e
    }
}

fun MapCells.update(point: Point, node: Node): MapCells {
    return this.with(point.x, this[point.x].with(point.y, node))
}

fun MapCells.update(point: Point, modifier: Node.() -> Node): MapCells {
    return this.with(point.x, this[point.x].with(point.y, modifier(this.get(point))))
}
