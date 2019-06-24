package icfp2019.model

data class Node(
    val point: Point,
    val isObstacle: Boolean,
    val isWrapped: Boolean = false,
    val hasTeleporterPlanted: Boolean = false,
    val booster: Booster? = null
) {
    fun hasBooster(booster: Booster): Boolean {
        if (this.booster != null) {
            return this.booster == booster
        }
        return false
    }

    fun isBooster(): Boolean {
        return this.booster != null
    }
}
