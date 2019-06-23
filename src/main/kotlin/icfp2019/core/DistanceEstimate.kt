package icfp2019.core

data class DistanceEstimate(val distance: Int) : Comparable<DistanceEstimate> {
    override fun compareTo(other: DistanceEstimate): Int =
        this.distance.compareTo(other.distance)
}
