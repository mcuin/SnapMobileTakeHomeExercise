package com.snapmobile.snapmobilerpncalculator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SnapMobileRPNCalculatorApp() {
    Column(modifier = Modifier.fillMaxSize()) {
        SnapMobileRPNCalculatorScreen(modifier = Modifier)
    }
}