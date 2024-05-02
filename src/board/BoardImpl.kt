package board

import board.Direction.*

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(createSquareBoard(width))


class SquareBoardImpl(override val width: Int) : SquareBoard {

    private val cells: List<List<Cell>> = List(width) { i -> List(width) { j -> Cell(i + 1, j + 1) } }

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        return if (i > width || j > width) null else cells[i - 1][j - 1]
    }

    override fun getCell(i: Int, j: Int): Cell {
        require(!(i > width || j > width)) { "The requested cell does not exist in the board!" }
        return cells[i - 1][j - 1]
    }

    override fun getAllCells(): Collection<Cell> {
        return cells.flatten()
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        return getLimitedRange(jRange).map { cells[i - 1][it - 1] }
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        return getLimitedRange(iRange).map { cells[it - 1][j - 1] }
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        return when (direction) {
            UP -> if (i == 1) null else cells[i - 2][j - 1]
            LEFT -> if (j == 1) null else cells[i - 1][j - 2]
            DOWN -> if (i == width) null else cells[i][j - 1]
            RIGHT -> if (j == width) null else cells[i - 1][j]
        }
    }

    private fun getLimitedRange(range: IntProgression): IntProgression {
        return if (range.step > 0)
            range.first..(if (range.last > width) width else range.last) step range.step
        else
            (if (range.first > width) width else range.first) downTo range.last step -range.step
    }
}

class GameBoardImpl<T>(private val squareBoard: SquareBoard) : GameBoard<T>, SquareBoard by squareBoard {

    private val cellMap: MutableMap<Cell, T?> = mutableMapOf()

    init {
        getAllCells().forEach {
            cellMap[it] = null
        }
    }

    override fun get(cell: Cell): T? {
        return cellMap[cell]
    }

    override fun swapCell(cell: Cell, direction: Direction) {

        val secondCell = cell.getNeighbour(direction.reversed())
        val secondCellValue = cellMap[secondCell]

        if(secondCell != null) {
            set(secondCell, cellMap[cell])
            set(cell, secondCellValue)
        }
    }

    override fun hasValidNeighbour(cell: Cell): Boolean {
        val value = this[cell]

        val neighboursList: MutableList<T?> = mutableListOf()

        if (cell.getNeighbour(UP) != null) {
            neighboursList.add(this[cell.getNeighbour(UP)!!])
        }

        if (cell.getNeighbour(DOWN) != null) {
            neighboursList.add(this[cell.getNeighbour(DOWN)!!])
        }

        if (cell.getNeighbour(LEFT) != null) {
            neighboursList.add(this[cell.getNeighbour(LEFT)!!])
        }

        if (cell.getNeighbour(RIGHT) != null) {
            neighboursList.add(this[cell.getNeighbour(RIGHT)!!])
        }

        return neighboursList.any { it == null || it == value }
    }

    override fun set(cell: Cell, value: T?) {
        cellMap[cell] = value
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        return cellMap.values.all(predicate)
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        return cellMap.values.any(predicate)
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
        return cellMap.filterValues(predicate).keys.firstOrNull()
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        return cellMap.filterValues(predicate).keys
    }
}
