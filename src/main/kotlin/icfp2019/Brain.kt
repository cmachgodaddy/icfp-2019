package icfp2019

import icfp2019.core.DistanceEstimate
import icfp2019.core.Strategy
import icfp2019.core.applyAction
import icfp2019.model.Action
import icfp2019.model.GameState
import icfp2019.model.Problem
import icfp2019.model.RobotId

fun strategySequence(
    initialGameState: GameState,
    strategy: Strategy,
    robotId: RobotId,
    initialAction: Action = Action.DoNothing
): Sequence<Pair<GameState, Action>> {
    return generateSequence(
        seed = initialGameState to initialAction,
        nextFunction = { (gameState, _) ->
            if (gameState.isGameComplete()) null
            else {
                val nextAction = strategy.compute(gameState)(robotId, gameState)
                val nextState = applyAction(gameState, robotId, nextAction)
                nextState to nextAction
            }
        }
    ).drop(1) // skip the initial state
}

data class BrainScore(
    val robotId: RobotId,
    val gameState: GameState,
    val action: Action,
    val distanceEstimate: DistanceEstimate,
    val strategy: Strategy
)

fun Sequence<Pair<GameState, Action>>.score(
    robotId: RobotId,
    strategy: Strategy
): BrainScore {
    // grab the first and last game state in this simulation path
    val (initial) = map { it to it.first }
        .reduce { (initial, _), (_, final) -> initial to final }

    // return the initial game state, if this path is the winner
    // we can use this to avoid duplicate action evaluation

    // DISABLE THIS

    return BrainScore(
        robotId,
        initial.first,
        initial.second,
        DistanceEstimate(0),
        strategy
    )
}

fun brainStep(
    initialGameState: GameState,
    strategy: Strategy,
    maximumSteps: Int
): Pair<GameState, Map<RobotId, Action>> {

    // one time step consists of advancing each worker wrapper by N moves
    // this will determine the order in which to advance robots by running
    // all robot states through all strategies, picking the the winning
    // robot/state pair and resuming until the stack of robots is empty

    // the list of robots can change over time steps, get a fresh copy each iteration
    var gameState = initialGameState
    val actions = mutableMapOf<RobotId, Action>()
    val workingSet = gameState.allRobotIds.toMutableSet()
    while (!gameState.isGameComplete() && workingSet.isNotEmpty()) {
        // pick the minimum across all robot/strategy pairs
        val winner = workingSet
            .map { robotId ->
                strategySequence(gameState, strategy, robotId)
                    .take(maximumSteps)
                    .score(robotId, strategy)
            }

        val winner0 = winner.minBy { it.distanceEstimate }!!

        // we have a winner, remove it from the working set for this time step
        workingSet.remove(winner0.robotId)
        // record the winning action and update the running state
        actions[winner0.robotId] = winner0.action
        gameState = winner0.gameState
    }

    return gameState to actions
}

fun brain(
    problem: Problem,
    strategy: Strategy,
    maximumSteps: Int
): Sequence<Solution> =
    generateSequence(
        seed = GameState(problem) to mapOf<RobotId, List<Action>>(),
        nextFunction = { (gameState, actions) ->
            if (gameState.isGameComplete()) {
                null
            } else {
                val (newState, newActions) = brainStep(gameState, strategy, maximumSteps)
                val mergedActions = actions.toMutableMap()
                newActions.forEach { (robotId, action) ->
                    mergedActions.merge(robotId, listOf(action)) { left, right -> left.plus(right) }
                }

                newState to mergedActions.toMap()
            }
        }
    ).map { (_, actions) -> Solution(problem, actions) }
