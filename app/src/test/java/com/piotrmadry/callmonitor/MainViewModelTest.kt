package com.piotrmadry.callmonitor

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.piotrmadry.callmonitor.datamodel.LogCompactDataModel
import com.piotrmadry.callmonitor.item.CallItem
import com.piotrmadry.callmonitor.item.ServerInfoItem
import com.piotrmadry.callmonitor.usecase.CallHistoryUseCase
import com.piotrmadry.callmonitor.utils.NetworkUtils
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private val networkUtils = mockk<NetworkUtils>()
    private val callHistory = mockk<CallHistoryUseCase>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }


    @Test
    fun `ensure items loaded correctly for non empty logs`() = runTest {

        every { networkUtils.getLocalIPAddressWithPort() } returns "some IP"
        every { callHistory.getLogCompact() } returns listOf(
            LogCompactDataModel(
                id = "id",
                contactName = "Jon",
                durationInSeconds = "0"
            )
        )

        val viewModel = create()

        viewModel.getData()

        val items = viewModel.items.getOrAwaitValue()

        Assert.assertTrue(items.size == 2)

        Assert.assertTrue(items[0] is ServerInfoItem)
        Assert.assertTrue(items[1] is CallItem)
    }

    @Test
    fun `ensure items loaded correctly for empty logs`() = runTest {

        every { networkUtils.getLocalIPAddressWithPort() } returns "some IP"
        every { callHistory.getLogCompact() } returns listOf()

        val viewModel = create()

        viewModel.getData()

        val items = viewModel.items.getOrAwaitValue()

        Assert.assertTrue(items.size == 1)

        Assert.assertTrue(items[0] is ServerInfoItem)
    }

    @Test
    fun `ensure progress works correctly`() = runTest {

        every { networkUtils.getLocalIPAddressWithPort() } returns "some IP"
        every { callHistory.getLogCompact() } returns listOf()

        val viewModel = create()

        Assert.assertTrue(viewModel.progress.getOrAwaitValue())

        viewModel.getData()

        Assert.assertFalse(viewModel.progress.getOrAwaitValue())
    }

    private fun create(): MainViewModel = MainViewModel(
        callHistory = callHistory,
        networkUtils = networkUtils,
        dispatcherIo = testDispatcher
    )
}