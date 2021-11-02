package com.quadible.paging

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import app.cash.turbine.FlowTurbine
import app.cash.turbine.test
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineScope

private class SameActionListUpdateCallback(
    private val onChange: () -> Unit
) : ListUpdateCallback {

    override fun onInserted(position: Int, count: Int) {
        onChange()
    }

    override fun onRemoved(position: Int, count: Int) {
        onChange()
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        onChange()
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        onChange()
    }
}

private class NoDiffCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = false
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = false
}

@ExperimentalCoroutinesApi
suspend fun <T : Any> Flow<PagingData<T>>.testPages(
    validate: suspend Librarian<T>.() -> Unit
) {
    this.test {
        validate(TurbineLibrarian(this))
    }
}

interface Librarian<T : Any> {

    suspend fun awaitPages(): Pagination<T>

    suspend fun ignoreRemaining()

    suspend fun awaitNoMore()
}

@ExperimentalCoroutinesApi
private class TurbineLibrarian<T : Any>(
    private val turbine: FlowTurbine<PagingData<T>>
) : Librarian<T> {

    override suspend fun awaitPages(): Pagination<T> =
        turbine.awaitItem().toPagination()

    override suspend fun ignoreRemaining(): Unit = turbine.cancelAndIgnoreRemainingEvents()

    override suspend fun awaitNoMore(): Unit = turbine.expectNoEvents()
}

private suspend fun <T : Any> PagingData<T>.toPagination(): Pagination<T> {
    val pages: MutableList<List<T>> = mutableListOf()
    var currentPage: MutableList<T> = mutableListOf()

    val currentPosition = MutableStateFlow(0)
    val updateCallback = SameActionListUpdateCallback {
        pages.add(currentPage)
        currentPosition.value = currentPosition.value + currentPage.size
        currentPage = mutableListOf()
    }

    val differ = AsyncPagingDataDiffer<T>(
        diffCallback = NoDiffCallback(),
        updateCallback = updateCallback,
        mainDispatcher = Dispatchers.Main,
        workerDispatcher = Dispatchers.Default
    )

    currentPosition.filter { it > 0 }
        .onEach { differ.getItem(it - 1) }
        .launchIn(TestCoroutineScope())

    try {
        withTimeout(5) {
            differ.submitData(this@toPagination.onEach { currentPage.add(it) })
        }
    } catch (e: TimeoutCancellationException) {
        // Ignore exception we just need it in order to stop
        // the underlying implementation blocking the main thread
    }

    return Pagination(pages = pages)
}

private fun <T : Any> PagingData<T>.onEach(action: (T) -> Unit): PagingData<T> = this.map {
    action(it)
    it
}

data class Pagination<T : Any>(private val pages: MutableList<List<T>>) {

    val isEmpty: Boolean = pages.isEmpty()

    val count: Int = pages.count()

    fun first(): List<T> = pages[0]

    fun last(): List<T> = pages[count - 1]

    fun pageAt(position: Int): List<T> = pages[position]

    fun loadedAt(point: Int): List<T> = pages.take(point).flatten()

    fun fullyLoaded(): List<T> = pages.last()
}
