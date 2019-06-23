package icfp2019.model

import org.pcollections.PVector

data class Problem(
    val size: MapSize,
    val startingPosition: Point,
    val map: PVector<PVector<Node>>
) {
    fun get(x: Int, y: Int): Node = map[x][y]
    fun get(point: Point): Node = map[point.x][point.y]
}
