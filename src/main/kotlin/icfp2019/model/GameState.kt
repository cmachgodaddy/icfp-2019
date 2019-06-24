package icfp2019.model

import icfp2019.core.MapCells
import icfp2019.core.get
import icfp2019.core.rebuild
import icfp2019.core.update
import org.pcollections.PVector

typealias BoardCells = PVector<PVector<BoardCell>>
typealias BoardNodeStates = PVector<PVector<BoardNodeState>>

data class BoardCell(
    val point: Point,
    val isObstacle: Boolean = false,
    val hasTeleporterPlanted: Boolean = false
) {
    constructor(node: Node) : this(node.point, node.isObstacle, node.hasTeleporterPlanted)
}

data class BoardNodeState(
    val point: Point,
    val isWrapped: Boolean = false,
    val booster: Booster? = null
) {
    constructor(node: Node) : this(node.point, node.isWrapped, node.booster)

    val hasBooster: Boolean = booster != null

    fun hasBooster(booster: Booster): Boolean {
        if (this.booster != null) {
            return this.booster == booster
        }
        return false
    }
}

fun BoardCells.allCells(): Sequence<BoardCell> = this.flatten().asSequence().map { it!! }
fun BoardNodeStates.allStates(): Sequence<BoardNodeState> = this.flatten().asSequence().map { it!! }

data class GameState private constructor(
    private val board: BoardCells,
    private val boardState: BoardNodeStates,
    val mapSize: MapSize,
    val startingPoint: Point,
    private val robotStates: Map<RobotId, RobotState> = initialRobotMap(startingPoint),
    val teleportDestination: List<Point> = listOf(),
    val unusedBoosters: Map<Booster, Int> = mapOf()
) {

    constructor(problem: Problem) : this(
        initBoardNodes(problem.map),
        initBoardNodeState(problem.map, problem.startingPosition),
        problem.size,
        problem.startingPosition
    )

    companion object {
        private fun initialRobotMap(point: Point) = mapOf(RobotId.first to RobotState(RobotId.first, point))

        private fun initBoardNodes(mapCells: MapCells): BoardCells {
            return mapCells.rebuild { BoardCell(it) }
        }

        private fun initBoardNodeState(mapCells: MapCells, start: Point): BoardNodeStates {
            return mapCells.rebuild {
                BoardNodeState(it)
            }.update(start) { copy(isWrapped = true) }
        }
    }

    fun board(): BoardCells = board
    fun boardState(): BoardNodeStates = boardState

    fun nodeState(point: Point): BoardNodeState = when {
        isInBoard(point) -> boardState.get(point)
        else -> error("$point Not in board")
    }

    val allRobotIds: Set<RobotId> = robotStates.keys

    fun get(point: Point): BoardCell {
        if (!isInBoard(point)) {
            throw ArrayIndexOutOfBoundsException("Access out of game board")
        }
        return board[point.x][point.y]
    }

    fun isGameComplete(): Boolean {
        return board().allCells().all { it.isObstacle || nodeState(it.point).isWrapped }
    }

    fun isInBoard(point: Point): Boolean {
        return (point.x in 0 until mapSize.x && point.y in 0 until mapSize.y)
    }

    fun updateBoard(point: Point, value: BoardCell): GameState {
        if (!isInBoard(point)) {
            throw ArrayIndexOutOfBoundsException("Access out of game board")
        }
        val newCells = board.update(point) { value }
        return copy(board = newCells)
    }

    fun updateState(point: Point, value: BoardNodeState): GameState {
        if (!isInBoard(point)) {
            throw ArrayIndexOutOfBoundsException("Access out of game board")
        }
        val newCells = boardState.update(point) { value }
        return copy(boardState = newCells)
    }

    fun withNewRobot(): GameState {
        val newId = this.allRobotIds.maxBy { it.id }!!.nextId()
        return withRobotState(newId, RobotState(newId, this.startingPoint))
    }

    fun withRobotPosition(robotId: RobotId, point: Point): GameState {
        return withRobotState(robotId, robot(robotId).copy(currentPosition = point))
    }

    fun withRobotState(robotId: RobotId, robotState: RobotState): GameState {
        return copy(robotStates = robotStates.plus(robotId to robotState))
    }

    fun robot(robotId: RobotId): RobotState {
        return this.robotStates.getValue(robotId)
    }

    fun boostersAvailable(booster: Booster): Int {
        return this.unusedBoosters.getOrDefault(booster, 0)
    }
}
