package com.snapmobile.snapmobilerpncalculator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SnapMobileRPNCalculatorScreen(modifier: Modifier, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel = hiltViewModel()) {

    Scaffold { scaffoldPadding ->

        val numbersList = listOf(7, 8, 9, 4, 5, 6, 1, 2, 3, 0)
        val operatorsList = listOf("+", "-", "*", "/")
        val actionsList = listOf(painterResource(R.drawable.ic_backspace), painterResource(R.drawable.ic_space), painterResource(R.drawable.ic_enter))

        Column(modifier = modifier.fillMaxSize().padding(scaffoldPadding)) {
            Text(modifier = modifier.fillMaxWidth(), text = "Entry history and evaluation")
            Text(modifier = modifier.fillMaxWidth(), text = "Current entry")
            LazyRow(modifier.fillMaxWidth()) {
                items(operatorsList) {
                    Text(modifier = modifier.weight(1f), text = it)
                }
            }
            Row(modifier = modifier.fillMaxWidth()) {
                LazyVerticalGrid(modifier = modifier.weight(1f), columns = GridCells.Fixed(3)) {
                    items(numbersList.size) {
                        Text(text = numbersList[it].toString())
                    }
                }
                LazyColumn {
                    items(actionsList) {
                        Icon(painter = it, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SnapMobileRPNCalculatorScreenPreview() {
    SnapMobileRPNCalculatorScreen(modifier = Modifier)
}