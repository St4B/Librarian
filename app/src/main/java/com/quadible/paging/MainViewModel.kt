package com.quadible.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class MainViewModel : ViewModel() {

    companion object {
        const val PAGE_SIZE = 3
    }

    private var order: DummyPagingSource.OrderMode = DummyPagingSource.OrderMode.ASC

    private lateinit var dummyPagingSource: DummyPagingSource

    val dummyData: Flow<PagingData<DummyData>> = Pager(
        PagingConfig(pageSize = PAGE_SIZE)
    ) {
        DummyPagingSource(
            pageSize = PAGE_SIZE,
            order = order
        ).also { dummyPagingSource = it }
    }.flow

    fun reverse() {
        order = when (order) {
            DummyPagingSource.OrderMode.ASC -> DummyPagingSource.OrderMode.DESC
            DummyPagingSource.OrderMode.DESC -> DummyPagingSource.OrderMode.ASC
        }
        dummyPagingSource.invalidate()
    }
}

class MainViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass == MainViewModel::class.java) {
            MainViewModel() as T
        } else {
            throw IllegalArgumentException("Not supported ViewModel")
        }
}