package icfp2019

import icfp2019.model.Action
import icfp2019.model.RobotId

data class Solution(val actions: Map<RobotId, List<Action>>) {
    override fun toString(): String {
        return actions.encodeActions()
    }
}

fun Iterable<Action>.encodeActions(): String =
    joinToString(separator = "") { it.toSolutionString() }

fun Map<RobotId, Iterable<Action>>.encodeActions(): String =
    entries.sortedBy { it.key.id }
        .joinToString(separator = "#") { it.value.encodeActions() }
