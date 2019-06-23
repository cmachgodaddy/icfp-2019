package icfp2019

import icfp2019.analyzers.ConservativeDistanceAnalyzer
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
            val nextAction = strategy.compute(gameState)(robotId, gameState)
            val nextState = applyAction(gameState, robotId, nextAction)
            nextState to nextAction
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
    val (initial, final) = map { it to it.first }
        .reduce { (initial, _), (_, final) -> initial to final }

    // from the final position we will estimate the number of
    // steps required to completely wrap the remainder of the mine
    val point = final.robotState.getValue(robotId).currentPosition
    val gameState = initial.first
    val conservativeDistance = ConservativeDistanceAnalyzer.analyze(gameState)(robotId, final)(point)

    // return the initial game state, if this path is the winner
    // we can use this to avoid duplicate action evaluation
    return BrainScore(
        robotId,
        initial.first,
        initial.second,
        conservativeDistance.estimate,
        strategy)
}

fun brainStep(
    initialGameState: GameState,
    strategies: Iterable<Strategy>,
    maximumSteps: Int
): Pair<GameState, Map<RobotId, Action>> {

    // one time step consists of advancing each worker wrapper by N moves
    // this will determine the order in which to advance robots by running
    // all robot states through all strategies, picking the the winning
    // robot/state pair and resuming until the stack of robots is empty

    // the list of robots can change over time steps, get a fresh copy each iteration
    var gameState = initialGameState
    val actions = mutableMapOf<RobotId, Action>()
    val workingSet = gameState.robotState.keys.toMutableSet()
    while (!gameState.isGameComplete() && workingSet.isNotEmpty()) {
        // pick the minimum across all robot/strategy pairs
        val winner = workingSet
            .flatMap { robotId ->
                strategies
                    .map { strategy ->
                        strategySequence(gameState, strategy, robotId)
                            .take(maximumSteps)
                            .takeWhile { !it.first.isGameComplete() }
                            .score(robotId, strategy)
                    }
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
    strategies: Iterable<Strategy>,
    maximumSteps: Int
): String {
    var gameState = GameState.gameStateOf(problem)
    val actions = mutableMapOf<RobotId, List<Action>>()
    while (!gameState.isGameComplete()) {
        val (newState, newActions) = brainStep(gameState, strategies, maximumSteps)

        gameState = newState
        newActions.forEach { (robotId, action) ->
            actions.merge(robotId, listOf(action)) { left, right -> left.plus(right) }
        }
    }

    return actions.encodeActions()
}
