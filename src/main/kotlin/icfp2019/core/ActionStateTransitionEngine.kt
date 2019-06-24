package icfp2019.core

import icfp2019.model.*

fun applyAction(gameState: GameState, robotId: RobotId, action: Action): GameState {
    val currentPosition = gameState.robot(robotId).currentPosition
    return with(gameState) {
        when (action) {
            Action.DoNothing -> this
            Action.MoveUp -> move(robotId, Point::up)
            Action.MoveDown -> move(robotId, Point::down)
            Action.MoveLeft -> move(robotId, Point::left)
            Action.MoveRight -> move(robotId, Point::right)
            Action.TurnClockwise -> updateRobot(robotId) {
                copy(orientation = orientation.turnClockwise())
            }
            Action.TurnCounterClockwise -> updateRobot(robotId) {
                copy(orientation = orientation.turnCounterClockwise())
            }
            Action.AttachFastWheels -> updateRobot(robotId) {
                copy(remainingFastWheelTime = this.remainingFastWheelTime + 50)
            }.useItem(Booster.FastWheels)
            Action.StartDrill -> updateRobot(robotId) {
                copy(remainingDrillTime = this.remainingDrillTime + 30)
            }.useItem(Booster.Drill)
            Action.PlantTeleportResetPoint -> updateMap(
                currentPosition,
                get(currentPosition).copy(hasTeleporterPlanted = true)
            ).useItem(Booster.Teleporter)
            Action.CloneRobot -> addRobot()

            is Action.TeleportBack -> updateRobot(robotId) {
                copy(currentPosition = action.targetResetPoint)
            }.useItem(Booster.Teleporter)

            is Action.AttachManipulator -> updateRobot(robotId) {
                copy(armRelativePoints = armRelativePoints.plus(action.point))
            }.useItem(Booster.ExtraArm)
        }
    }
}

private fun GameState.addRobot(): GameState {
    val newId = RobotId(this.robotState.keys.maxBy { it.id }!!.id + 1)
    return copy(robotState = robotState.plus(newId to RobotState(newId, this.startingPoint)))
}

private fun GameState.move(robotId: RobotId, mover: (Point) -> Point): GameState {
    val robotState = this.robotState.getValue(robotId)

    val distance = if (robotState.hasActiveFastWheels()) 2 else 1

    return (0 until distance).fold(this) { state, _ ->
        val newPosition = mover.invoke(state.robot(robotId).currentPosition)
        state.updateRobot(robotId) {
            copy(currentPosition = newPosition)
        }.let { it.wrapAffectedCells(robotId) }
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

private fun GameState.useItem(booster: Booster): GameState {
    return this.copy(unusedBoosters = unusedBoosters.plus(booster to unusedBoosters.getOrDefault(booster, 1) - 1))
}

private fun GameState.pickupBoosters(point: Point): GameState {
    val node = this.get(point)
    val booster = node.booster ?: return this
    // pickup
    return this.updateMap(point, node.copy(booster = null)).let {
        it.copy(unusedBoosters = it.unusedBoosters.plus(booster to it.unusedBoosters.getOrDefault(booster, 0) + 1))
    }
}

private fun GameState.wrapAffectedCells(robotId: RobotId): GameState {
    val robot = this.robot(robotId)
    val robotPoint = robot.currentPosition
    val cells = this.cells.update(robotPoint, cells.get(robotPoint).copy(isWrapped = true, isObstacle = false))
    val rcells = robot.armRelativePoints.fold(cells, { acc, p ->
        val newPoint = robotPoint.applyRelativePoint(p)
        if (this.isInBoard(newPoint) && cells.get(newPoint).isObstacle.not()) {
            acc.update(newPoint, cells.get(newPoint).copy(isWrapped = true))
        } else {
            acc
        }
    })
    return this.copy(cells = rcells)
}

private fun GameState.updateMap(point: Point, node: Node): GameState {
    return this.copy(cells = this.cells.update(point, node))
}
