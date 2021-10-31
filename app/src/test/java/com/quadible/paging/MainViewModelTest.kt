package com.quadible.paging

import app.cash.turbine.test
import com.quadible.paging.MainViewModel.Companion.PAGE_SIZE
import junit.framework.Assert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.math.ceil

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
        viewModel.dummyData.test {
            // Then
            // ceil because a page may contain les items than the page's size
            val pages = ceil(pagedData.size.toDouble() / PAGE_SIZE).toInt()

            val testSnapshots = awaitItem().toTestSnapshots()
            repeat(pages) {
                assertEquals(
                    pagedData.take(PAGE_SIZE * it),
                    testSnapshots.atPoint(point = it)
                )
            }
        }
    }

    @Test
    fun `should reverse pages in batches`() = runBlockingTest {
        // Given
        val viewModel = MainViewModel()

        // When
        viewModel.dummyData.test {
            viewModel.reverse()

            // Then
            // ceil because a page may contain les items than the page's size
            val pages = ceil(pagedData.size.toDouble() / PAGE_SIZE).toInt()

            val testSnapshots = expectMostRecentItem().toTestSnapshots()
            repeat(pages) {
                assertEquals(
                    pagedData.reversed().take(PAGE_SIZE * it),
                    testSnapshots.atPoint(point = it)
                )
            }
        }
    }

    @Test
    fun `should load pages in batches when we trigger many times to reverse sorting`() =
        runBlockingTest {
            // Given
            val viewModel = MainViewModel()

            // When
            viewModel.dummyData.test {
                // Then
                // ceil because a page may contain les items than the page's size
                val pages = ceil(pagedData.size.toDouble() / PAGE_SIZE).toInt()

                viewModel.reverse()
                val testSnapshots1stReverse = expectMostRecentItem().toTestSnapshots()
                repeat(pages) {
                    assertEquals(
                        pagedData.reversed().take(PAGE_SIZE * it),
                        testSnapshots1stReverse.atPoint(point = it)
                    )
                }

                viewModel.reverse()
                val testSnapshots2stReverse = awaitItem().toTestSnapshots()
                repeat(pages) {
                    assertEquals(
                        pagedData.take(PAGE_SIZE * it),
                        testSnapshots2stReverse.atPoint(point = it)
                    )
                }

                viewModel.reverse()
                val testSnapshots3stReverse = awaitItem().toTestSnapshots()
                repeat(pages) {
                    assertEquals(
                        pagedData.reversed().take(PAGE_SIZE * it),
                        testSnapshots3stReverse.atPoint(point = it)
                    )
                }
            }
        }
}