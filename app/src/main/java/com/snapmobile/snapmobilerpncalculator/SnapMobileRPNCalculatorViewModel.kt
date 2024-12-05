package com.snapmobile.snapmobilerpncalculator

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Stack
import javax.inject.Inject

@HiltViewModel
class SnapMobileRPNCalculatorViewModel @Inject constructor(): ViewModel() {

    val operatorsList = listOf("+", "-", "*", "/")
    val historicalEntry = mutableStateOf("")
    val currentEntry = mutableStateOf("")

    fun updateCurrentEntry(newCharacter: String) {
        currentEntry.value += newCharacter
    }

    fun updateHistoricalEntry() {
        val trimmedCurrentEntry = currentEntry.value.trimEnd().replace("\\s+".toRegex(), " ")
        if (historicalEntry.value.isNotEmpty()) {
            historicalEntry.value += " $trimmedCurrentEntry"
        } else {
            historicalEntry.value = trimmedCurrentEntry
        }
        if (operatorsList.contains(historicalEntry.value.last().toString())) {
            evaluate()
        }

        currentEntry.value = ""
    }

    fun clearCurrentEntry() {
        currentEntry.value = ""
    }

    fun clearHistoricalEntry() {
        historicalEntry.value = ""
    }

    fun backspace() {
        currentEntry.value = currentEntry.value.dropLast(1)
    }

    private fun evaluate() {
        val enteredCharacters = historicalEntry.value.split(" ")
        val numbersStack = Stack<Int>()

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
                    numbersStack.push(firstNumber / secondNumber)
                }
                else -> numbersStack.push(character.toInt())
            }
        }

        historicalEntry.value = numbersStack.joinToString(" ")
    }
}