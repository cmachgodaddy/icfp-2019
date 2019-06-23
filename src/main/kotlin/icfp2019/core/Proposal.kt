package icfp2019.core

import icfp2019.model.Action

data class Proposal(val estimatedDistance: DistanceEstimate, val nextMove: Action) : Comparable<Proposal> {
    companion object {
        private val comparator = Comparator
            .comparingInt<Proposal> { it.estimatedDistance.distance }
    }

    override fun compareTo(other: Proposal): Int {
        return comparator.compare(this, other)
    }
}
