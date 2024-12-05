package com.snapmobile.snapmobilerpncalculator

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DecimalFormat
import java.util.Stack
import javax.inject.Inject

@HiltViewModel
class SnapMobileRPNCalculatorViewModel @Inject constructor(): ViewModel() {

    val operatorsList = listOf("+", "-", "*", "/")
    val historicalEntry = mutableStateOf("")
    val currentEntry = mutableStateOf("")
    var showDecimal = false

    fun updateCurrentEntry(newCharacter: String) {
        currentEntry.value += newCharacter
    }

    fun updateHistoricalEntry() {
        evaluate(historicalEntry.value, currentEntry.value)
        currentEntry.value = ""
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

    private fun evaluate(previousHistoricalEntry: String, currentEntry: String) {
        val trimmedCurrentEntry = previousHistoricalEntry + currentEntry.trimEnd().replace("\\s+".toRegex(), " ")
        val enteredCharacters = trimmedCurrentEntry.split(" ")
        val numbersStack = Stack<Double>()

        for (character in enteredCharacters) {
            when (character) {
                "+" -> {
                    val secondNumber = numbersStack.pop()
                    val firstNumber = numbersStack.pop()
                    numbersStack.push(firstNumber + secondNumber)
                }
                "-" -> {
                    val secondNumber = numbersStack.pop()
                    val firstNumber = numbersStack.pop()
                    numbersStack.push(firstNumber - secondNumber)
                }
                "*" -> {
                    val secondNumber = numbersStack.pop()
                    val firstNumber = numbersStack.pop()
                    numbersStack.push(firstNumber * secondNumber)
                }
                "/" -> {
                    val secondNumber = numbersStack.pop()
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
    }
}