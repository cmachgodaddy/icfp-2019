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
                copy(armRelativePoints = gameState.robot(robotId).turnClockwise())
            }

            Action.TurnCounterClockwise -> updateRobot(robotId) {
                copy(orientation = orientation.turnCounterClockwise())
                copy(armRelativePoints = gameState.robot(robotId).turnCounterClockwise())
            }

            Action.AttachFastWheels -> updateRobot(robotId) {
                copy(remainingFastWheelTime = this.remainingFastWheelTime + 50)
            }.useBoosterFromState(Booster.FastWheels)

            Action.StartDrill -> updateRobot(robotId) {
                copy(remainingDrillTime = this.remainingDrillTime + 30)
            }.useBoosterFromState(Booster.Drill)

            Action.PlantTeleportResetPoint -> updateBoard(
                currentPosition,
                get(currentPosition).copy(hasTeleporterPlanted = true)
            ).useBoosterFromState(Booster.Teleporter)

            Action.CloneRobot -> withNewRobot()

            is Action.TeleportBack -> updateRobot(robotId) {
                copy(currentPosition = action.targetResetPoint)
            }

            is Action.AttachManipulator -> updateRobot(robotId) {
                copy(armRelativePoints = armRelativePoints.plus(action.point))
            }.useBoosterFromState(Booster.ExtraArm)
        }
    }
}

private fun GameState.move(robotId: RobotId, mover: (Point) -> Point): GameState {
    val robotState = this.robot(robotId)

    val distance = if (robotState.hasActiveFastWheels()) 2 else 1

    val movedState = (0 until distance).fold(this) { state, _ ->
        val newPosition = mover(state.robot(robotId).currentPosition)
        if (!state.isInBoard(newPosition) || state.get(newPosition).isObstacle) {
            state
        } else {
            state.updateRobot(robotId) { copy(currentPosition = newPosition) }
                .wrapAffectedCells(robotId)
                .addBoosterToState(newPosition)
        }
    }
    return movedState.updateRobot(robotId) {
        copy(
            remainingFastWheelTime = if (robotState.hasActiveFastWheels()) robotState.remainingFastWheelTime - 1 else 0,
            remainingDrillTime = if (robotState.hasActiveDrill()) robotState.remainingDrillTime - 1 else 0
        )
    }
}

private fun GameState.updateRobot(robotId: RobotId, update: RobotState.() -> RobotState): GameState {
    val robotState = update.invoke(this.robot(robotId))
    return this.withRobotState(robotId, robotState)
}

private fun GameState.useBoosterFromState(booster: Booster): GameState {
    return this.copy(unusedBoosters = unusedBoosters.plus(booster to unusedBoosters.getOrDefault(booster, 1) - 1))
}

private fun GameState.addBoosterToState(point: Point): GameState {
    val node = this.nodeState(point)
    val booster = node.booster ?: return this
    // pickup
    return this.updateState(point, node.copy(booster = null)).let {
        it.copy(unusedBoosters = it.unusedBoosters.plus(booster to it.unusedBoosters.getOrDefault(booster, 0) + 1))
    }
}

private fun GameState.wrapAffectedCells(robotId: RobotId): GameState {
    val robot = this.robot(robotId)
    val robotPoint = robot.currentPosition
    val boardNode = this.get(robotPoint)

    val withUpdatedBoardState = if (boardNode.isObstacle)
        updateBoard(robotPoint, boardNode.copy(isObstacle = false))
    else this

    val updatedState = withUpdatedBoardState.updateState(robotPoint, this.nodeState(robotPoint).copy(isWrapped = true))

    fun computeClosestWallOnRobotPath(seq: IntProgression): List<Pair<Int, Boolean>> {
        return seq
            .map { it to robotPoint.applyRelativePoint(Point(0, it)) }
            .filter { updatedState.isInBoard(it.second) }
            .map { it.first to updatedState.get(it.second).isObstacle }
            .filter { it.second }
    }
    val closestWallOnRobotPathUp = computeClosestWallOnRobotPath(1 until 9)
    val closestWallOnRobotPathDown = computeClosestWallOnRobotPath(-1 downTo -9)

    val maxVisibleForWallOnRobotPathUp = if (closestWallOnRobotPathUp.isEmpty()) {
        Int.MAX_VALUE
    } else {
        val x = closestWallOnRobotPathUp.first().let { it.first }
        (x * 2) - 1
    }

    val maxVisibleForWallOnRobotPathDown = if (closestWallOnRobotPathDown.isEmpty()) {
        Int.MAX_VALUE
    } else {
        val x = closestWallOnRobotPathDown.first().let { it.first }
        (x * -2) - 1
    }

    fun computeClosestWallOnArmPath(seq: IntProgression): List<Pair<Int, Boolean>> {
        return seq
            .map { it to robotPoint.applyRelativePoint(Point(1, it)) }
            .filter { updatedState.isInBoard(it.second) }
            .map { it.first to updatedState.get(it.second).isObstacle }
            .filter { it.second }
    }

    val closestWallOnArmPathUp = computeClosestWallOnArmPath(1 until 9)
    val closestWallOnArmPathDown = computeClosestWallOnArmPath(-1 downTo -9)

    val shadowsForArmPathUp = if (closestWallOnArmPathUp.isEmpty()) {
        listOf(0)
    } else {
        val x = closestWallOnArmPathUp.first().let { it.first }
        x until x + x + 1
    }

    val shadowsForArmPathDown = if (closestWallOnArmPathDown.isEmpty()) {
        listOf(0)
    } else {
        val x = closestWallOnArmPathDown.first().let { it.first } * -1
        x until x + x + 1
    }

    fun isArmPointVisible(armRelativePoint: Point): Boolean {
        if (armRelativePoint.y > 1) {
            val armLength = armRelativePoint.y
            return (armLength <= maxVisibleForWallOnRobotPathUp)
        } else if (armRelativePoint.y < 0) {
            val armLength = armRelativePoint.y * -1
            return (armLength <= maxVisibleForWallOnRobotPathDown)
        } else {
            return true
        }
    }

    fun isArmPointVisibleDueToArmPathWall(armRelativePoint: Point): Boolean {
        if (armRelativePoint.y > 1) {
            val armY = armRelativePoint.y
            return (armY in shadowsForArmPathUp).not()
        } else if (armRelativePoint.y < 0) {
            val armY = armRelativePoint.y * -1
            return (armY in shadowsForArmPathDown).not()
        } else {
            return true
        }
    }

    return robot.armRelativePoints.fold(updatedState, { state, armRelativePoint ->
        val armWorldPoint = robotPoint.applyRelativePoint(armRelativePoint)
        if (state.isInBoard(armWorldPoint) && isArmPointVisible(armRelativePoint) && isArmPointVisibleDueToArmPathWall(armRelativePoint) && state.get(armWorldPoint).isObstacle.not()) {
            val boardState = state.nodeState(armWorldPoint)
            state.updateState(armWorldPoint, boardState.copy(isWrapped = true))
        } else {
            state
        }
    })
}
