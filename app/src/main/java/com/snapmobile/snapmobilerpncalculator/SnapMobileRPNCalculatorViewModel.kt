package com.snapmobile.snapmobilerpncalculator

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SnapMobileRPNCalculatorViewModel @Inject constructor(): ViewModel() {

    val historicalEntry = mutableStateOf("")
    val currentEntry = mutableStateOf("")

    fun updateCurrentEntry(newCharacter: String) {
        currentEntry.value += newCharacter
    }

    fun updateHistoricalEntry() {
        historicalEntry.value = currentEntry.value
        currentEntry.value = ""
    }

    fun clearCurrentEntry() {
        currentEntry.value = ""
    }

    fun clearHistoricalEntry() {
        historicalEntry.value = ""
    }
}