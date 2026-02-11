package fr.epechassieu.carnetdechant.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A JUnit Test Rule that swaps the main dispatcher with a test dispatcher.
 * This is useful for testing coroutines that use `Dispatchers.Main`.
 *
 * It replaces the main dispatcher before the test runs and resets it after the test finishes.
 *
 * Example usage in a test class:
 * ```
 * @get:Rule
 * val mainDispatcherRule = MainDispatcherRule()
 *
 * @Test
 * fun myTest() = runTest {
 *     // Your test code that launches coroutines on Dispatchers.Main
 * }
 * ```
 *
 * @param testDispatcher The dispatcher to use as the main dispatcher during tests.
 *                       Defaults to a `StandardTestDispatcher`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule (
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher(){
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
        }
}