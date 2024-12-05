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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SnapMobileRPNCalculatorScreen(modifier: Modifier, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel = hiltViewModel()) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }) { scaffoldPadding ->

        val context = LocalContext.current
        val evaluateError = snapMobileRPNCalculatorViewModel.evaluateError.collectAsStateWithLifecycle(null)
        val numbersList = listOf(7, 8, 9, 4, 5, 6, 1, 2, 3, 0)

        LaunchedEffect(evaluateError.value) {
            if (evaluateError.value != null) {
                when (evaluateError.value) {
                    is EvaluateErrors.DivideByZeroError -> {
                        snackbarHostState.showSnackbar(context.getString(R.string.divide_by_zero_error))
                        snapMobileRPNCalculatorViewModel.clearEvaluateError()
                    }
                    is EvaluateErrors.UnevenNotationError -> {
                        snackbarHostState.showSnackbar(context.getString(R.string.uneven_notation_error))
                        snapMobileRPNCalculatorViewModel.clearEvaluateError()
                    }
                    null -> {}
                }
            }
        }

        Column(modifier = modifier.fillMaxSize().padding(scaffoldPadding)) {
            UserHistoryText(modifier = modifier, snapMobileRPNCalculatorViewModel = snapMobileRPNCalculatorViewModel)
            CurrentEntryText(modifier = modifier, snapMobileRPNCalculatorViewModel = snapMobileRPNCalculatorViewModel)
            LazyVerticalGrid(modifier = modifier, columns = GridCells.Fixed(4)) {
                items(snapMobileRPNCalculatorViewModel.operatorsList) {operator ->
                    OperationButton(modifier = modifier, operator = operator, snapMobileRPNCalculatorViewModel = snapMobileRPNCalculatorViewModel)
                }
            }
            Row(modifier = modifier.fillMaxWidth()) {
                LazyVerticalGrid(modifier = modifier.weight(1f), columns = GridCells.Fixed(3)) {
                    items(numbersList) { number ->
                        NumberButton(modifier, number = number, snapMobileRPNCalculatorViewModel = snapMobileRPNCalculatorViewModel)
                    }
                }
                Column(modifier = modifier) {
                    BackspaceButton(modifier = modifier, snapMobileRPNCalculatorViewModel = snapMobileRPNCalculatorViewModel)
                    SpaceButton(modifier = modifier, snapMobileRPNCalculatorViewModel = snapMobileRPNCalculatorViewModel)
                    EnterButton(modifier = modifier, snapMobileRPNCalculatorViewModel = snapMobileRPNCalculatorViewModel, scope = coroutineScope)
                }
            }
        }
    }
}

@Composable
fun UserHistoryText(modifier: Modifier, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel) {

    TextField(modifier = modifier.fillMaxWidth(),
        value = snapMobileRPNCalculatorViewModel.historicalEntry.value,
        onValueChange = {},
        readOnly = true,
        trailingIcon = {
            if (snapMobileRPNCalculatorViewModel.historicalEntry.value.isNotEmpty()) {
                Icon(modifier = modifier.padding(8.dp).clickable {
                    snapMobileRPNCalculatorViewModel.clearHistoricalEntry()
                },
                    imageVector = Icons.Default.Clear,
                    contentDescription = null)
            }
        })
}

@Composable
fun CurrentEntryText(modifier: Modifier, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel) {

    TextField(modifier = modifier.fillMaxWidth(),
        value = snapMobileRPNCalculatorViewModel.currentEntry.value,
        onValueChange = {},
        readOnly = true,
        trailingIcon = {
            if (snapMobileRPNCalculatorViewModel.currentEntry.value.isNotEmpty()) {
                Icon(modifier = modifier.padding(8.dp).clickable {
                    snapMobileRPNCalculatorViewModel.clearCurrentEntry()
                },
                    imageVector = Icons.Default.Clear,
                    contentDescription = null)
            }
        })
}

@Composable
fun NumberButton(modifier: Modifier, number: Int, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel) {
    OutlinedButton(modifier = modifier.padding(8.dp), onClick = {
        snapMobileRPNCalculatorViewModel.updateCurrentEntry(number.toString())
    }, shape = CircleShape) {
        Text(text = number.toString())
    }
}

@Composable
fun OperationButton(modifier: Modifier, operator: String, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel) {
    OutlinedButton(modifier = modifier.padding(8.dp), onClick = {
        snapMobileRPNCalculatorViewModel.updateCurrentEntry(operator)
    },
        shape = CircleShape) {
        Text(text = operator)
    }
}

@Composable
fun EnterButton(modifier: Modifier, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel, scope: CoroutineScope) {
    OutlinedButton(modifier = modifier.padding(8.dp), onClick = {
        scope.launch {
            snapMobileRPNCalculatorViewModel.updateHistoricalEntry()
        }
    }, shape = CircleShape) {
        Icon(painter = painterResource(R.drawable.ic_enter), contentDescription = null)
    }
}

@Composable
fun SpaceButton(modifier: Modifier, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel) {
    OutlinedButton(modifier = modifier.padding(8.dp), onClick = {
        snapMobileRPNCalculatorViewModel.updateCurrentEntry(" ")
    }, shape = CircleShape) {
        Icon(painter = painterResource(R.drawable.ic_space), contentDescription = null)
    }
}

@Composable
fun BackspaceButton(modifier: Modifier, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel) {
    OutlinedButton(modifier = modifier.padding(8.dp), onClick = {
        snapMobileRPNCalculatorViewModel.backspace()
    }, shape = CircleShape) {
        Icon(painter = painterResource(R.drawable.ic_backspace), contentDescription = null)
    }
}

@Preview
@Composable
fun SnapMobileRPNCalculatorScreenPreview() {
    SnapMobileRPNCalculatorScreen(modifier = Modifier)
}