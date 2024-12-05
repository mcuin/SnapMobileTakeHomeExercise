package com.snapmobile.snapmobilerpncalculator

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SnapMobileRPNCalculatorViewModelTests {

    private lateinit var viewModel: SnapMobileRPNCalculatorViewModel

    @Before
    fun setup() {
        viewModel = SnapMobileRPNCalculatorViewModel()
    }

    @Test
    fun testEvaluateOnce() = runTest {
        viewModel.evaluate(viewModel.historicalEntry.value, "2 3 +")
        assertEquals(viewModel.historicalEntry.value, "5")
    }

    @Test
    fun testEvaluateTwice() = runTest {
        viewModel.evaluate(viewModel.historicalEntry.value, "2 3 +")
        viewModel.evaluate(viewModel.historicalEntry.value, "4 *")
        assertEquals(viewModel.historicalEntry.value, "20")
    }

    @Test
    fun testEvaluateThrice() = runTest {
        viewModel.evaluate(viewModel.historicalEntry.value, "2")
        viewModel.evaluate(viewModel.historicalEntry.value, "3")
        viewModel.evaluate(viewModel.historicalEntry.value, "*")
        assertEquals(viewModel.historicalEntry.value, "6")
    }

    @Test
    fun testEvaluateDecimal() = runTest {
        viewModel.evaluate(viewModel.historicalEntry.value, "-3")
        assertEquals(viewModel.historicalEntry.value, "-3.0")
    }
}