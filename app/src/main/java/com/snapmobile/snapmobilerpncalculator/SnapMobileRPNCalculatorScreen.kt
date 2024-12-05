package com.snapmobile.snapmobilerpncalculator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * SnapMobileRPNCalculatorScreen for showing the calculator options, and main point of interaction for the user.
 * @param modifier Modifier to be applied to the screen.
 * @param snapMobileRPNCalculatorViewModel ViewModel to be used for the calculator provided by way of Hilt injection.
 */
@Composable
fun SnapMobileRPNCalculatorScreen(modifier: Modifier, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel = hiltViewModel()) {

    // Remember a CoroutineScope to be able to launch suspend functions on the viewmodel
    val coroutineScope = rememberCoroutineScope()

    // Remember a SnackbarHostState to be able to show snackbars
    val snackbarHostState = remember { SnackbarHostState() }

    /**
     * Screen level scaffold for the calculator
     * @param modifier Modifier to be applied to the scaffold.
     * @param snackbarHostState SnackbarHostState to be used for showing snackbars on the scaffold.
     */
    Scaffold(modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }) { scaffoldPadding ->

        // Get the context from the current composition
        val context = LocalContext.current
        // Collect the state from the viewmodel and convert it to a state
        val evaluateError = snapMobileRPNCalculatorViewModel.evaluateError.collectAsStateWithLifecycle(null)
        // List of numbers to be used to build the calculator number buttons
        val numbersList = listOf(7, 8, 9, 4, 5, 6, 1, 2, 3, 0)
        // Operations list to be shown on the calculator as well as
        val operatorsList = listOf("+", "-", "*", "/")

        /**
         * Launched effect to handle the evaluate error state and show a snackbar on the screen when there is an error
         * @param evaluateError State to be observed for changes
         */
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
                    is EvaluateErrors.InvalidCharacterError -> {
                        snackbarHostState.showSnackbar(context.getString(R.string.invalid_character_error))
                        snapMobileRPNCalculatorViewModel.clearEvaluateError()
                    }
                    null -> {}
                }
            }
        }

        /**
         * Full screen column to hold the calculator UI elements
         * @param modifier Modifier to be applied to the column.
         */
        Column(modifier = modifier.fillMaxSize().padding(scaffoldPadding)) {
            UserHistoryText(modifier = modifier.weight(1f), snapMobileRPNCalculatorViewModel = snapMobileRPNCalculatorViewModel)
            CurrentEntryText(modifier = modifier.weight(2f), snapMobileRPNCalculatorViewModel = snapMobileRPNCalculatorViewModel)
            LazyVerticalGrid(modifier = modifier, columns = GridCells.Fixed(4)) {
                items(operatorsList) {operator ->
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

/**
 * UserHistoryText composable for displaying the user entered history of the calculator
 * @param modifier Modifier to be applied to the text field.
 * @param snapMobileRPNCalculatorViewModel Viewmodel to access current user history and clear the history when needed
 */
@Composable
fun UserHistoryText(modifier: Modifier, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel) {

    TextField(modifier = modifier.fillMaxWidth(), textStyle = TextStyle(fontSize = MaterialTheme.typography.displaySmall.fontSize),
        value = snapMobileRPNCalculatorViewModel.historicalEntry.value,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        trailingIcon = {
            if (snapMobileRPNCalculatorViewModel.historicalEntry.value.isNotEmpty()) {
                Icon(modifier = modifier.padding(dimensionResource(R.dimen.basic_padding)).clickable {
                    snapMobileRPNCalculatorViewModel.clearHistoricalEntry()
                },
                    imageVector = Icons.Default.Clear,
                    contentDescription = null)
            }
        })
}

/**
 * CurrentEntryText composable for displaying the current entry in the calculator
 * @param modifier Modifier to be applied to the text field.
 * @param snapMobileRPNCalculatorViewModel Viewmodel to access current entry and clear the entry when needed
 */
@Composable
fun CurrentEntryText(modifier: Modifier, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel) {

    TextField(modifier = modifier.fillMaxWidth(),textStyle = TextStyle(fontSize = MaterialTheme.typography.displayLarge.fontSize),
        value = snapMobileRPNCalculatorViewModel.currentEntry.value,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        trailingIcon = {
            if (snapMobileRPNCalculatorViewModel.currentEntry.value.isNotEmpty()) {
                Icon(modifier = modifier.padding(dimensionResource(R.dimen.basic_padding)).clickable {
                    snapMobileRPNCalculatorViewModel.clearCurrentEntry()
                },
                    imageVector = Icons.Default.Clear,
                    contentDescription = null)
            }
        })
}

/**
 * NumberButton composable for displaying the number buttons on the calculator
 * @param modifier Modifier to be applied to the button.
 * @param number Number to be displayed on the button and be used as the value of the button when clicked
 * @param snapMobileRPNCalculatorViewModel Viewmodel to update the current entry when the button is clicked
 */
@Composable
fun NumberButton(modifier: Modifier, number: Int, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel) {
    Button(modifier = modifier.padding(8.dp), onClick = {
        snapMobileRPNCalculatorViewModel.updateCurrentEntry(number.toString())
    }, shape = CircleShape) {
        Text(text = number.toString())
    }
}

/**
 * OperationButton composable for displaying the operation buttons on the calculator
 * @param modifier Modifier to be applied to the button.
 * @param operator Operator to be displayed on the button and be used as the value of the button when clicked
 * @param snapMobileRPNCalculatorViewModel Viewmodel to update the current entry when the button is clicked
 */
@Composable
fun OperationButton(modifier: Modifier, operator: String, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel) {
    Button(modifier = modifier.padding(8.dp), onClick = {
        snapMobileRPNCalculatorViewModel.updateCurrentEntry(operator)
    },
        shape = CircleShape) {
        Text(text = operator)
    }
}

/**
 * EnterButton composable for displaying the enter button on the calculator
 * @param modifier Modifier to be applied to the button.
 * @param snapMobileRPNCalculatorViewModel Viewmodel to evaluate and update the historical entry when the button is clicked
 * @param scope CoroutineScope to be used to launch suspend functions in the viewmodel
 */
@Composable
fun EnterButton(modifier: Modifier, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel, scope: CoroutineScope) {
    OutlinedButton(modifier = modifier.padding(dimensionResource(R.dimen.basic_padding)), onClick = {
        scope.launch {
            snapMobileRPNCalculatorViewModel.updateHistoricalEntry()
        }
    }, shape = CircleShape) {
        Icon(painter = painterResource(R.drawable.ic_enter), contentDescription = null)
    }
}

/**
 * SpaceButton composable for displaying the space button on the calculator
 * @param modifier Modifier to be applied to the button.
 * @param snapMobileRPNCalculatorViewModel Viewmodel to update the current entry with a space when the button is clicked
 */
@Composable
fun SpaceButton(modifier: Modifier, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel) {
    OutlinedButton(modifier = modifier.padding(dimensionResource(R.dimen.basic_padding)), onClick = {
        snapMobileRPNCalculatorViewModel.updateCurrentEntry(" ")
    }, shape = CircleShape) {
        Icon(painter = painterResource(R.drawable.ic_space), contentDescription = null)
    }
}

/**
 * BackspaceButton composable for displaying the backspace button on the calculator
 * @param modifier Modifier to be applied to the button.
 * @param snapMobileRPNCalculatorViewModel Viewmodel to remove the most recent character from the current entry when the button is clicked
 */
@Composable
fun BackspaceButton(modifier: Modifier, snapMobileRPNCalculatorViewModel: SnapMobileRPNCalculatorViewModel) {
    OutlinedButton(modifier = modifier.padding(dimensionResource(R.dimen.basic_padding)), onClick = {
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