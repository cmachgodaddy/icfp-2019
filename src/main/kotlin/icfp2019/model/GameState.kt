package icfp2019.model

import icfp2019.core.MapCells
import icfp2019.core.update

data class GameState private constructor(
    val cells: MapCells,
    val mapSize: MapSize,
    val startingPoint: Point,
    val robotState: Map<RobotId, RobotState> = initialRobotMap(startingPoint),
    val teleportDestination: List<Point> = listOf(),
    val unusedBoosters: Map<Booster, Int> = mapOf()
) {
    constructor(problem: Problem) : this(
        initStartNodeWrap(problem.map, problem.startingPosition),
        problem.size,
        problem.startingPosition
    )

    companion object {
        private fun initialRobotMap(point: Point) = mapOf(RobotId.first to RobotState(RobotId.first, point))
        // HACK for now, wrap start node - find a better place for this??
        private fun initStartNodeWrap(mapCells: MapCells, start: Point): MapCells {
            return mapCells.update(start) {
                copy(isWrapped = true)
            }
        }
    }

    fun isGameComplete(): Boolean {
        return cells.flatten().all { it.isObstacle || it.isWrapped }
    }

    fun isInBoard(point: Point): Boolean {
        return (point.x in 0 until mapSize.x && point.y in 0 until mapSize.y)
    }

    fun get(point: Point): Node {
        if (!isInBoard(point)) {
            throw ArrayIndexOutOfBoundsException("Access out of game board")
        }
        return cells[point.x][point.y]
    }

    fun set(point: Point, value: Node): GameState {
        if (!isInBoard(point)) {
            throw ArrayIndexOutOfBoundsException("Access out of game board")
        }
        val newCells = cells.with(point.x, cells[point.x].with(point.y, value))
        return copy(cells = newCells)
    }

    fun withRobotPosition(robotId: RobotId, point: Point): GameState {
        return copy(robotState = robotState.plus(robotId to robotState.getValue(robotId).copy(currentPosition = point)))
    }

    fun robot(robotId: RobotId): RobotState {
        return this.robotState.getValue(robotId)
    }

    fun boostersAvailable(booster: Booster): Int {
        return this.unusedBoosters.getOrDefault(booster, 0)
    }
}
