package icfp2019.analyzers

import icfp2019.core.Analyzer
import icfp2019.model.*

object MoveAnalyzer : Analyzer<(RobotId, Action) -> Boolean> {
    override fun analyze(initialState: GameState): (robotId: RobotId, state: GameState) -> (RobotId, Action) -> Boolean {
        return { _, gameState ->
            { robotId, action ->
                var possible = false

                val robotState = gameState.robotState[robotId]
                if (robotState != null &&
                    initialState.isInBoard(robotState.currentPosition)
                ) {
                    val cell = initialState.get(robotState.currentPosition)

                    fun hasBooster(booster: Booster): Boolean {
                        return gameState.unusedBoosters.contains(booster)
                    }

                    fun canMoveTo(point: Point): Boolean {
                        return initialState.isInBoard(point) &&
                                (!cell.isObstacle || hasBooster(Booster.Drill))
                    }

                    fun canTeleportTo(point: Point): Boolean {
                        return gameState.teleportDestination.contains(point)
                    }

                    possible = when (action) {
                        Action.MoveUp -> canMoveTo(robotState.currentPosition.up())
                        Action.MoveDown -> canMoveTo(robotState.currentPosition.down())
                        Action.MoveLeft -> canMoveTo(robotState.currentPosition.left())
                        Action.MoveRight -> canMoveTo(robotState.currentPosition.right())
                        Action.DoNothing -> true
                        Action.TurnClockwise -> true
                        Action.TurnCounterClockwise -> true
                        is Action.AttachManipulator -> hasBooster(Booster.ExtraArm)
                        Action.AttachFastWheels -> hasBooster(Booster.FastWheels)
                        Action.StartDrill -> hasBooster(Booster.Drill)
                        Action.PlantTeleportResetPoint -> hasBooster(Booster.Teleporter)
                        is Action.TeleportBack -> canTeleportTo(action.targetResetPoint)
                        Action.CloneRobot -> hasBooster(Booster.CloneToken) &&
                                initialState.get(robotState.currentPosition).hasBooster(Booster.CloningLocation)
                    }
                }
                possible
            }
        }
    }
}
