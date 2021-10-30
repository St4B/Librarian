package com.quadible.paging

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
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
suspend fun <T : Any> PagingData<T>.toTestSnapshots(): TestPageSnapshots<T> {
    val snapshots: MutableList<List<T>> = mutableListOf()
    var currentSnapshot: MutableList<T> = mutableListOf()

    val currentPosition = MutableStateFlow(0)
    val updateCallback = SameActionListUpdateCallback {
        snapshots.add(currentSnapshot)
        currentPosition.value = currentPosition.value + currentSnapshot.size
        currentSnapshot = mutableListOf()
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
        withTimeout(50000) {
            differ.submitData(
                this@toTestSnapshots.onEach { currentSnapshot.add(it) }
            )
        }
    } catch (e: TimeoutCancellationException) {
        // Ignore exception we just need it in order to stop
        // the underlying implementation blocking the main thread
    }

    return TestPageSnapshots(snapshots = snapshots)
}

class TestPageSnapshots<T>(private val snapshots: MutableList<List<T>>) {

    fun atPoint(point: Int): List<T> = snapshots.take(point).flatten()

    fun latest(): List<T> = snapshots.last()
}

private fun <T : Any> PagingData<T>.onEach(action: (T) -> Unit): PagingData<T> = this.map {
    action(it)
    it
}