package icfp2019.model

data class MapSize(val x: Int, val y: Int) {
    fun pointInMap(p: Point): Boolean {
        return p.x >= 0 && p.x < x && p.y >= 0 && p.y < y
    }
}
