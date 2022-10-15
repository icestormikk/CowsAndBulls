package com.example.functions

import com.example.domain.HistoryNote
import com.example.domain.StatisticsNote
import kotlin.math.pow
import kotlin.properties.Delegates
@Suppress("TooManyFunctions")
object CowsAndBulls {
    private val statisticsData = mutableListOf<StatisticsNote>()

    private var sequenceLength by Delegates.notNull<Int>()
    private val history = mutableListOf<HistoryNote>()
    private val sequenceOfNumbers = mutableListOf<String>()
    private var realAnswer: String? = null

    /**
     * Sets the initial values for the elements of the game "Cows and Bulls".
     * @param sequenceLength limitation on the length of numbers in a sequence
     * @param answer the number to be guessed
     */
    fun initializeGame(sequenceLength: Int, answer: String? = null) {
        history.clear()
        sequenceOfNumbers.clear()

        generateSequence(sequenceLength)
        this.sequenceLength = sequenceLength

        realAnswer = answer ?: "${(10.0.pow(sequenceLength - 1).toLong()..10.0.pow(sequenceLength).toLong())
            .random()}"
    }

    fun clearGame() {
        listOf(history,sequenceOfNumbers).forEach { it.clear() }
        sequenceLength = 1
        realAnswer = null
    }

    fun fetchCompatibleNumber(): String {
        while (true) {
            val number = fetchRandomNumber()

            if (number.isCompatible())
                return number
        }
    }

    /**
     * Generates a sequence of numbers of length [length] that does not contain duplicate digits.
     * @param length Length limit
     * @return List<String>
     */
    private fun generateSequence(length: Int) {
        if (length < 1)
            error("Length parameter cannot be non-positive: $length")

        for (digit in 10.0.pow(length - 1).toLong()..10.0.pow(length).toLong()) {
            val stringDigit = "$digit".toCharArray()

            if (stringDigit.size == stringDigit.toSet().size)
                sequenceOfNumbers.add(
                    stringDigit.joinToString(separator = "")
                )
        }
    }

    /**
     * Counts the number of "cows" (numbers that are in both the word [guess] and the word [realAnswer])
     * and "bulls" (numbers that are in both words and stand in their places).
     * @param guess the intended answer
     * @param realAnswer
     * @return Pair<Int, Int>
     */
    private fun cowsAndBullsCounter(
        guess: String,
        realAnswer: String,
    ): Pair<Int, Int> =
        Pair(
            first = guess.count { realAnswer.contains(it) },
            second = guess.withIndex().count { realAnswer.indexOf(it.value) == it.index }
        )

    /**
     * Checking whether this number can be a candidate for an answer.
     * @receiver the intended answer
     * @return returns true if the number can be a candidate for the answer
     */
    private fun String.isCompatible(): Boolean =
        history.all { (previousGuess, cowsCount, bullsCount) ->
            cowsAndBullsCounter(this, previousGuess) == cowsCount to bullsCount
        }

    /**
     * Extracts a random number from a sequence.
     * @return String
     */
    private fun fetchRandomNumber(): String = sequenceOfNumbers.random().also {
        sequenceOfNumbers.remove(it)
    }

    /**
     * Creates a new entry in the history of moves.
     * @param newBlank new entry
     * @return true
     */
    fun addToHistory(newBlank: HistoryNote) =
        history.add(newBlank)

    /**
     * Returns the last state of the history of moves at the times of calling
     * the function.
     * @return MutableList<Triple<String, Int, Int>>
     */
    fun getHistorySnapshot() =
        history

    fun getActualSequence(): List<String> =
        sequenceOfNumbers

    fun getStatistics(): List<StatisticsNote> =
        statisticsData

    fun addStatistics(statisticsNote: StatisticsNote) =
        statisticsData.add(statisticsNote)

    fun initiateStatistics(statisticsNotes: List<StatisticsNote>) =
        statisticsData.addAll(statisticsNotes)
}
