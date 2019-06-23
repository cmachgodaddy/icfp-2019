package icfp2019.model

enum class Orientation {
    Up, Down, Left, Right;

    fun turnClockwise() = when (this) {
        Up -> Right
        Right -> Down
        Down -> Left
        Left -> Up
    }

    fun turnCounterClockwise() = this.turnClockwise().turnClockwise().turnClockwise()
}
