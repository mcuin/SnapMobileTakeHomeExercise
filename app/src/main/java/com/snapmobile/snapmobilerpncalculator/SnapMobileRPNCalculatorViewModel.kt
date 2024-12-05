package com.snapmobile.snapmobilerpncalculator

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import java.text.DecimalFormat
import java.util.Stack
import javax.inject.Inject

/**
 * Viewmodel for the calculator to hold state and business logic
 */
@HiltViewModel
class SnapMobileRPNCalculatorViewModel @Inject constructor(): ViewModel() {

    // State variables to hold the current and historical entries
    val historicalEntry = mutableStateOf("")
    val currentEntry = mutableStateOf("")
    // Flag to show decimal points for negative or remainder numbers
    private var showDecimal = false
    // Viewmodel private flow to emit invalid evaluation errors
    private val _evaluateError = MutableSharedFlow<EvaluateErrors?>()
    // Public flow to observe invalid evaluation errors in the UI
    val evaluateError = _evaluateError

    /**
     * Updates the current entry with a new character
     * @param newCharacter The character to be added to the current entry
     */
    fun updateCurrentEntry(newCharacter: String) {
        currentEntry.value += newCharacter
    }

    /**
     * Evaluate both historical and current entries
     */
    suspend fun updateHistoricalEntry() {
        evaluate(historicalEntry.value, currentEntry.value)
    }

    /**
     * Clears the current entry
     */
    fun clearCurrentEntry() {
        currentEntry.value = ""
        showDecimal = false
    }

    /**
     * Clears the historical entry
     */
    fun clearHistoricalEntry() {
        historicalEntry.value = ""
        showDecimal = false
    }

    /**
     * Deletes the last character from the current entry
     */
    fun backspace() {
        currentEntry.value = currentEntry.value.dropLast(1)
    }

    /**
     * Emits an error for uneven notations if found during evaluation
     */
    private suspend fun unevenNotations() {
        _evaluateError.emit(EvaluateErrors.UnevenNotationError)
    }

    /**
     * Clears the evaluate error so the snackbar can appear again for other errors
     */
    suspend fun clearEvaluateError() {
        _evaluateError.emit(null)
    }

    /**
     * Evaluates the current and previous entries using the shunting yard algorithm, and checks for errors
     * @param previousHistoricalEntry The previous historical entry
     * @param newCurrentEntry The new user entry
     */
    suspend fun evaluate(previousHistoricalEntry: String, newCurrentEntry: String) {
        // Combine both historical and current entries, and remove any unnecessary spaces in the new entry
        val trimmedCurrentEntry = if (previousHistoricalEntry.isNotEmpty()) {
                previousHistoricalEntry +  " " + newCurrentEntry.trimEnd().replace("\\s+".toRegex(), " ")
            } else {
                newCurrentEntry.trimEnd().replace("\\s+".toRegex(), " ")
        }
        val enteredCharacters = trimmedCurrentEntry.split(" ")
        var numbersCount = 0
        val numbersStack = Stack<Double>()

        for (character in enteredCharacters) {
            when (character) {
                "+" -> {
                    if (numbersCount < 2) {
                        unevenNotations()
                        return
                    }
                    val secondNumber = numbersStack.pop()
                    val firstNumber = numbersStack.pop()
                    numbersStack.push(firstNumber + secondNumber)
                }
                "-" -> {
                    if (numbersCount < 2) {
                        unevenNotations()
                        return
                    }
                    val secondNumber = numbersStack.pop()
                    val firstNumber = numbersStack.pop()
                    numbersStack.push(firstNumber - secondNumber)
                }
                "*" -> {
                    if (numbersCount < 2) {
                        unevenNotations()
                        return
                    }
                    val secondNumber = numbersStack.pop()
                    val firstNumber = numbersStack.pop()
                    numbersStack.push(firstNumber * secondNumber)
                }
                "/" -> {
                    if (numbersCount < 2) {
                        unevenNotations()
                        return
                    }
                    val secondNumber = numbersStack.pop()
                    if (secondNumber == 0.0) {
                        _evaluateError.emit(EvaluateErrors.DivideByZeroError)
                        return
                    }
                    val firstNumber = numbersStack.pop()
                    if (!showDecimal) {
                        showDecimal = firstNumber % secondNumber != 0.0
                    }
                    numbersStack.push(firstNumber / secondNumber)
                }
                else -> {
                    try {
                        character.toDouble()
                    } catch (e: NumberFormatException) {
                        _evaluateError.emit(EvaluateErrors.InvalidCharacterError)
                        return
                    }
                    if (!showDecimal) {
                        showDecimal = character.toDouble() < 0
                    }
                    numbersStack.push(character.toDouble())
                    numbersCount++
                }
            }
        }

        // Decimal formatting for negative and remainder numbers
        val decimalFormat = if (showDecimal) {
            DecimalFormat("#.0##")
        } else {
            DecimalFormat("#")
        }
        var stackString = ""
        for (number in numbersStack) {
            stackString += " " + decimalFormat.format(number)
        }
        historicalEntry.value = stackString.trimStart()
        currentEntry.value = ""
    }
}

/**
 * Sealed class to represent different types of errors that can occur during evaluation
 */
sealed class EvaluateErrors {
    data object DivideByZeroError: EvaluateErrors()
    data object UnevenNotationError: EvaluateErrors()
    data object InvalidCharacterError: EvaluateErrors()
}