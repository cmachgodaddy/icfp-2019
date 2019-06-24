package icfp2019.core

import icfp2019.model.Node
import icfp2019.model.Point
import org.pcollections.PVector
import org.pcollections.TreePVector

typealias MapCells = PVector<PVector<Node>>

fun <T> MapCells.rebuild(convert: (Node) -> T): PVector<PVector<T>> {
    return TreePVector.from(this.map {
        TreePVector.from(it.map { convert(it) })
    })
}

fun <T> PVector<PVector<T>>.get(point: Point): T {
    try {
        return this[point.x][point.y]
    } catch (e: Exception) {
        throw IllegalArgumentException("Illegal Access $point", e)
    }
}

fun <T> PVector<PVector<T>>.update(point: Point, modifier: T.() -> T): PVector<PVector<T>> {
    return this.with(point.x, this[point.x].with(point.y, modifier(this.get(point))))
}
