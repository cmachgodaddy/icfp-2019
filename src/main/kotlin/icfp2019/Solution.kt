package icfp2019

import icfp2019.model.Action
import icfp2019.model.Problem
import icfp2019.model.RobotId
import java.time.LocalDateTime

data class Solution(
    val problem: Problem,
    val actions: Map<RobotId, List<Action>>
) {
    val solutionTime = LocalDateTime.now()

    override fun toString(): String {
        return actions.encodeActions()
    }

    fun summary(): String {
        var maxSteps = 0
        actions.forEach {
            val actionSize = it.value.size
            if (actionSize > maxSteps) {
                maxSteps = actionSize
            }
        }
        return "Problem: ${problem.name} with size: ${problem.size.x}x${problem.size.y} solved on $solutionTime with $maxSteps steps"
    }
}

fun Iterable<Action>.encodeActions(): String =
    joinToString(separator = "") { it.toSolutionString() }

fun Map<RobotId, Iterable<Action>>.encodeActions(): String =
    entries.sortedBy { it.key.id }
        .joinToString(separator = "#") { it.value.encodeActions() }
