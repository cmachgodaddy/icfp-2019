package icfp2019.model

data class Node(
    val point: Point,
    val isObstacle: Boolean,
    val isWrapped: Boolean = false,
    val hasTeleporterPlanted: Boolean = false,
    val booster: Booster? = null
)
