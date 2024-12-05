package com.snapmobile.snapmobilerpncalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.snapmobile.snapmobilerpncalculator.ui.theme.SnapMobileRPNCalculatorTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * SnapMobileRPNCalculatorActivity that is the main entry point for the app and holds the main composable function.
 */
@AndroidEntryPoint
class SnapMobileRPNCalculatorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnapMobileRPNCalculatorTheme {
                SnapMobileRPNCalculatorApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SnapMobileRPNCalculatorTheme {
        SnapMobileRPNCalculatorApp()
    }
}