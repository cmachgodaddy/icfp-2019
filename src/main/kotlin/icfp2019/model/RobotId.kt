package icfp2019.model

data class RobotId private constructor(val id: Int) {
    companion object {
        val first = RobotId(0)
    }

    fun nextId(): RobotId {
        return this.copy(id = id + 1)
    }
}
