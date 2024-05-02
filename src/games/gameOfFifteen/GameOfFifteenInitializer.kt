package games.gameOfFifteen

import games.ui.GAME_WIDTH

interface GameOfFifteenInitializer {
    /*
     * Even permutation of numbers 1..15
     * used to initialize the first 15 cells on a board.
     * The last cell is empty.
     */
    val initialPermutation: List<Int>
}

class RandomGameInitializer : GameOfFifteenInitializer {
    /*
     * Generate a random permutation from 1 to 15.
     * `shuffled()` function might be helpful.
     * If the permutation is not even, make it even (for instance,
     * by swapping two numbers).
     */
    override val initialPermutation: List<Int> by lazy {
        var numbers: List<Int> = (1 until GAME_WIDTH * GAME_WIDTH).toList()

        do {
            numbers = numbers.shuffled()
        } while (!isEven(numbers))

        return@lazy numbers
    }
}

