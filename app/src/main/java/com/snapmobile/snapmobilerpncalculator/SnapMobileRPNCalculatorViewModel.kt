package com.snapmobile.snapmobilerpncalculator

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import java.text.DecimalFormat
import java.util.Stack
import javax.inject.Inject

@HiltViewModel
class SnapMobileRPNCalculatorViewModel @Inject constructor(): ViewModel() {

    val operatorsList = listOf("+", "-", "*", "/")
    val historicalEntry = mutableStateOf("")
    val currentEntry = mutableStateOf("")
    private var showDecimal = false
    private val _evaluateError = MutableSharedFlow<EvaluateErrors?>()
    val evaluateError = _evaluateError

    fun updateCurrentEntry(newCharacter: String) {
        currentEntry.value += newCharacter
    }

    suspend fun updateHistoricalEntry() {
        evaluate(historicalEntry.value, currentEntry.value)
    }

    fun clearCurrentEntry() {
        currentEntry.value = ""
        showDecimal = false
    }

    fun clearHistoricalEntry() {
        historicalEntry.value = ""
        showDecimal = false
    }

    fun backspace() {
        currentEntry.value = currentEntry.value.dropLast(1)
    }

    private suspend fun unevenNotations() {
        _evaluateError.emit(EvaluateErrors.UnevenNotationError)
    }

    suspend fun clearEvaluateError() {
        _evaluateError.emit(null)
    }

    private suspend fun evaluate(previousHistoricalEntry: String, newCurrentEntry: String) {
        val trimmedCurrentEntry = previousHistoricalEntry + newCurrentEntry.trimEnd().replace("\\s+".toRegex(), " ")
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
                    if (!showDecimal) {
                        showDecimal = character.toDouble() < 0
                    }
                    numbersStack.push(character.toDouble())
                    numbersCount++
                }
            }
        }
        val decimalFormat = if (showDecimal) {
            DecimalFormat("#.0##")
        } else {
            DecimalFormat("#")
        }
        var stackString = ""
        for (number in numbersStack) {
            stackString += decimalFormat.format(number) + " "
        }
        historicalEntry.value = stackString
        currentEntry.value = ""
    }
}

sealed class EvaluateErrors {
    data object DivideByZeroError: EvaluateErrors()
    data object UnevenNotationError: EvaluateErrors()
}