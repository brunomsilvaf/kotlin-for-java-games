package games.game2048

import board.Cell
import board.Direction
import board.GameBoard
import board.createGameBoard
import games.game.Game
import games.ui.GAME_WIDTH

/*
 * Your task is to implement the game 2048 https://en.wikipedia.org/wiki/2048_(video_game).
 * Implement the utility methods below.
 *
 * After implementing it you can try to play the game running 'PlayGame2048'.
 */
fun newGame2048(initializer: Game2048Initializer<Int> = RandomGame2048Initializer): Game = Game2048(initializer)

class Game2048(private val initializer: Game2048Initializer<Int>) : Game {
    private val board = createGameBoard<Int?>(GAME_WIDTH)

    override fun initialize() {
        repeat(2) {
            board.addNewValue(initializer)
        }
    }

    override fun canMove(): Boolean {
        return board.getAllCells().any { board.hasValidNeighbour(it) }
    }

    override fun hasWon() = board.any { it == 2048 }

    override fun processMove(direction: Direction) {
        if (board.moveValues(direction)) {
            board.addNewValue(initializer)
        }
    }

    override fun get(i: Int, j: Int): Int? = board.run { get(getCell(i, j)) }
}

/*
 * Add a new value produced by 'initializer' to a specified cell in a board.
 */
fun GameBoard<Int?>.addNewValue(initializer: Game2048Initializer<Int>) {
    val newValue = initializer.nextValue(this)
    if (newValue != null) {
        this[newValue.first] = newValue.second
    }
}

/*
 * Update the values stored in a board,
 * so that the values were "moved" in a specified rowOrColumn only.
 * Use the helper function 'moveAndMergeEqual' (in Game2048Helper.kt).
 * The values should be moved to the beginning of the row (or column),
 * in the same manner as in the function 'moveAndMergeEqual'.
 * Return 'true' if the values were moved and 'false' otherwise.
 */
fun GameBoard<Int?>.moveValuesInRowOrColumn(rowOrColumn: List<Cell>): Boolean {
    val rowOrColumnValues = rowOrColumn.map { this[it] }

    val rowOrColumnValuesAfterMove = rowOrColumnValues.moveAndMergeEqual { it * 2 }
    return if (rowOrColumnValuesAfterMove.isEmpty() || rowOrColumnValues == rowOrColumnValuesAfterMove) {
        false
    } else {
        rowOrColumn.forEachIndexed { index, cell ->
            this[cell] = if (index > rowOrColumnValuesAfterMove.lastIndex) null else rowOrColumnValuesAfterMove[index]
        }
        true
    }
}

/*
 * Update the values stored in a board,
 * so that the values were "moved" to the specified direction
 * following the rules of the 2048 game .
 * Use the 'moveValuesInRowOrColumn' function above.
 * Return 'true' if the values were moved and 'false' otherwise.
 */
fun GameBoard<Int?>.moveValues(direction: Direction): Boolean {

    var anyMoved = false
    for (i in 1..this.width) {
        val hasMoved = when (direction) {
            Direction.UP -> moveValuesInRowOrColumn(this.getColumn(1..this.width, i))
            Direction.DOWN -> moveValuesInRowOrColumn(this.getColumn(1..this.width, i).reversed())
            Direction.LEFT -> moveValuesInRowOrColumn(this.getRow(i, 1..this.width))
            Direction.RIGHT -> moveValuesInRowOrColumn(this.getRow(i, 1..this.width).reversed())
        }
        if (hasMoved) anyMoved = true
    }
    return anyMoved
}