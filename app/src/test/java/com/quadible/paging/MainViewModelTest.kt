package com.quadible.paging

import com.quadible.paging.MainViewModel.Companion.PAGE_SIZE
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.math.ceil
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
class MainViewModelTest {

    private val testCoroutineDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `should load pages in batches`() = runBlockingTest {
        // Given
        val viewModel = MainViewModel()

        // When
        viewModel.dummyData.testPages {

            // Then
            awaitPages().assertOrdered()
        }
    }

    @ExperimentalTime
    @Test
    fun `should reverse pages in batches`() = runBlockingTest {
        // Given
        val viewModel = MainViewModel()

        // When
        viewModel.dummyData.testPages {

            // Then
            awaitPages().assertOrdered()

            viewModel.reverse()
            awaitPages().assertReverseOrdered()
        }
    }

    @Test
    fun `should load pages in batches when we trigger many times to reverse sorting`() =
        runBlockingTest {
            // Given
            val viewModel = MainViewModel()

            // When
            viewModel.dummyData.testPages {
                // Then
                awaitPages().assertOrdered()

                viewModel.reverse()
                awaitPages().assertReverseOrdered()

                viewModel.reverse()
                awaitPages().assertOrdered()

                viewModel.reverse()
                awaitPages().assertReverseOrdered()
            }
        }

    private fun Pagination<DummyData>.assertOrdered() {
        // ceil because a page may contain les items than the page's size
        val pages = ceil(pagedData.size.toDouble() / PAGE_SIZE).toInt()

        repeat(pages) {
            assertEquals(pagedData.take(PAGE_SIZE * it), this.loadedAt(point = it))
        }
    }

    private fun Pagination<DummyData>.assertReverseOrdered() {
        // ceil because a page may contain les items than the page's size
        val pages = ceil(pagedData.size.toDouble() / PAGE_SIZE).toInt()

        repeat(pages) {
            assertEquals(pagedData.reversed().take(PAGE_SIZE * it), this.loadedAt(point = it))
        }
    }
}