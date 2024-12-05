package com.snapmobile.snapmobilerpncalculator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SnapMobileRPNCalculatorScreen(modifier: Modifier, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel = hiltViewModel()) {

    Scaffold { scaffoldPadding ->

        val numbersList = listOf(7, 8, 9, 4, 5, 6, 1, 2, 3, 0)
        val operatorsList = listOf("+", "-", "*", "/")

        Column(modifier = modifier.fillMaxSize().padding(scaffoldPadding)) {
            UserHistoryText(modifier)
            CurrentEntryText(modifier)
            LazyVerticalGrid(modifier = modifier, columns = GridCells.Fixed(4)) {
                items(operatorsList) {operator ->
                    OperationButton(modifier = modifier, operator = operator)
                }
            }
            Row(modifier = modifier.fillMaxWidth()) {
                LazyVerticalGrid(modifier = modifier.weight(1f), columns = GridCells.Fixed(3)) {
                    items(numbersList) { number ->
                        NumberButton(modifier, number = number)
                    }
                }
                Column(modifier = modifier) {
                    BackspaceButton(modifier)
                    SpaceButton(modifier)
                    EnterButton(modifier)
                }
            }
        }
    }
}

@Composable
fun UserHistoryText(modifier: Modifier) {

    TextField(modifier = modifier.fillMaxWidth(),
        value = "Entry history and evaluation",
        onValueChange = {},
        readOnly = true)
}

@Composable
fun CurrentEntryText(modifier: Modifier) {

    TextField(modifier = modifier.fillMaxWidth(),
        value = "Current entry",
        onValueChange = {},
        readOnly = true)
}

@Composable
fun NumberButton(modifier: Modifier, number: Int) {
    OutlinedButton(modifier = modifier.padding(8.dp), onClick = {}, shape = CircleShape) {
        Text(text = number.toString())
    }
}

@Composable
fun OperationButton(modifier: Modifier, operator: String) {
    OutlinedButton(modifier = modifier.padding(8.dp), onClick = {},
        shape = CircleShape) {
        Text(text = operator)
    }
}

@Composable
fun EnterButton(modifier: Modifier) {
    OutlinedButton(modifier = modifier.padding(8.dp), onClick = {}, shape = CircleShape) {
        Icon(painter = painterResource(R.drawable.ic_enter), contentDescription = null)
    }
}

@Composable
fun SpaceButton(modifier: Modifier) {
    OutlinedButton(modifier = modifier.padding(8.dp), onClick = {}, shape = CircleShape) {
        Icon(painter = painterResource(R.drawable.ic_space), contentDescription = null)
    }
}

@Composable
fun BackspaceButton(modifier: Modifier) {
    OutlinedButton(modifier = modifier.padding(8.dp), onClick = {}, shape = CircleShape) {
        Icon(painter = painterResource(R.drawable.ic_backspace), contentDescription = null)
    }
}

@Preview
@Composable
fun SnapMobileRPNCalculatorScreenPreview() {
    SnapMobileRPNCalculatorScreen(modifier = Modifier)
}