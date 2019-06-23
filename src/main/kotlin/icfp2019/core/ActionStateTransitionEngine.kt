package icfp2019.core

import icfp2019.model.*

fun applyAction(gameState: GameState, robotId: RobotId, action: Action): GameState {
    val currentPosition = gameState.robot(robotId).currentPosition
    return with(gameState) {
        when (action) {
            Action.MoveUp -> move(robotId, Point::up)
            Action.MoveDown -> move(robotId, Point::down)
            Action.MoveLeft -> move(robotId, Point::left)
            Action.MoveRight -> move(robotId, Point::right)
            Action.DoNothing -> this
            Action.TurnClockwise -> updateRobot(robotId) {
                copy(orientation = orientation.turnClockwise())
            }
            Action.TurnCounterClockwise -> updateRobot(robotId) {
                copy(orientation = orientation.turnCounterClockwise())
            }
            // TODO need to remove items from the game state after use
            Action.AttachFastWheels -> updateRobot(robotId) {
                copy(remainingFastWheelTime = this.remainingFastWheelTime + 50)
            }
            Action.StartDrill -> updateRobot(robotId) {
                copy(remainingDrillTime = this.remainingDrillTime + 30)
            }
            Action.PlantTeleportResetPoint -> updateMap(
                currentPosition,
                get(currentPosition).copy(hasTeleporterPlanted = true)
            )
            Action.CloneRobot -> addRobot()

            is Action.TeleportBack -> updateRobot(robotId) {
                copy(currentPosition = action.targetResetPoint)
            }
            is Action.AttachManipulator -> TODO()
        }
    }
}

private fun GameState.addRobot(): GameState {
    val newId = RobotId(this.robotState.keys.maxBy { it.id }!!.id + 1)
    return copy(robotState = robotState.plus(newId to RobotState(newId, this.startingPoint)))
}

private fun GameState.robot(robotId: RobotId): RobotState {
    return this.robotState.getValue(robotId)
}

private fun GameState.move(robotId: RobotId, mover: (Point) -> Point): GameState {
    val robotState = this.robotState.getValue(robotId)

    val distance = if (robotState.hasActiveFastWheels()) 2 else 1

    return (0 until distance).fold(this) { state, _ ->
        val newPosition = mover.invoke(state.robot(robotId).currentPosition)
        state.updateRobot(robotId) {
            copy(currentPosition = newPosition)
        }.let { it.wrapAffectedCells(newPosition) }
            .let { it.pickupBoosters(newPosition) }
    }.updateRobot(robotId) {
        copy(
            remainingFastWheelTime = if (robotState.hasActiveFastWheels()) robotState.remainingFastWheelTime - 1 else 0,
            remainingDrillTime = if (robotState.hasActiveDrill()) robotState.remainingDrillTime - 1 else 0
        )
    }
}

private fun GameState.updateRobot(robotId: RobotId, update: RobotState.() -> RobotState): GameState {
    val robotState = update.invoke(this.robotState.getValue(robotId))
    return this.copy(robotState = this.robotState.plus(robotState.robotId to robotState))
}

private fun GameState.pickupBoosters(point: Point): GameState {
    val node = this.get(point)
    val booster = node.booster ?: return this
    // pickup
    return this.updateMap(point, node.copy(booster = null)).let {
        it.copy(unusedBoosters = it.unusedBoosters.plus(booster))
    }
}

private fun GameState.wrapAffectedCells(point: Point): GameState {
    val cells = this.cells
    return this.copy(cells = cells.update(point, cells.get(point).copy(isWrapped = true, isObstacle = false)))
}

private fun GameState.updateMap(point: Point, node: Node): GameState {
    return this.copy(cells = this.cells.update(point, node))
}
