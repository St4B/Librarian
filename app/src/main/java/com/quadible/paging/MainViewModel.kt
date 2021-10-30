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

    private val dummyPagingSource = DummyPagingSource(pageSize = PAGE_SIZE)

    fun getDummyData(): Flow<PagingData<DummyData>> =
        Pager(PagingConfig(pageSize = PAGE_SIZE)) { dummyPagingSource }.flow
}

class MainViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass == MainViewModel::class.java) {
            MainViewModel() as T
        } else {
            throw IllegalArgumentException("Not supported ViewModel")
        }
}