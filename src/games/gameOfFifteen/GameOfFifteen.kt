package games.gameOfFifteen

import board.Cell
import board.Direction
import board.createGameBoard
import games.game.Game
import games.game2048.moveValues
import games.game2048.moveValuesInRowOrColumn
import games.ui.GAME_WIDTH

/*
 * Implement the Game of Fifteen (https://en.wikipedia.org/wiki/15_puzzle).
 * When you finish, you can play the game by executing 'PlayGameOfFifteen'.
 */
fun newGameOfFifteen(initializer: GameOfFifteenInitializer = RandomGameInitializer()): Game = GameOfFifteen(initializer)

class GameOfFifteen(private val initializer: GameOfFifteenInitializer) : Game {
    private val board = createGameBoard<Int?>(GAME_WIDTH)

    override fun initialize() {

        board.getAllCells().forEachIndexed { index, cell ->
            if (index != board.getAllCells().toList().lastIndex) {
                board[cell] = initializer.initialPermutation[index]
            }
        }
    }

    override fun canMove(): Boolean = true

    override fun hasWon(): Boolean {
        val winningList = (1 until GAME_WIDTH * GAME_WIDTH).toMutableList<Int?>()
        winningList.add(null)
        return board.getAllCells().map { board[it] }.toList() == winningList
    }

    override fun processMove(direction: Direction) {
        val nullCell: Cell = board.find { it == null }!!
        board.swapCell(nullCell, direction)
    }

    override fun get(i: Int, j: Int): Int? = board.run { get(getCell(i, j)) }
}


