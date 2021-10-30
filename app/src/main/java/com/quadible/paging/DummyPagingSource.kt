package com.quadible.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

class DummyPagingSource(
    private val pageSize: Int,
    private val order: OrderMode = OrderMode.ASC
) : PagingSource<Int, DummyData>() {

    enum class OrderMode { ASC, DESC }

    // prevKey == null -> anchorPage is the first page.
    // nextKey == null -> anchorPage is the last page.
    // both prevKey and nextKey null -> anchorPage is the initial page.
    override fun getRefreshKey(state: PagingState<Int, DummyData>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DummyData> = try {
        val pageNumber = when (params) {
            is LoadParams.Refresh -> 0
            else -> params.key ?: 0
        }
        val page = pagedData.sort(order = order)
            .chunked(size = pageSize)
            .getOrElse(index = pageNumber) { emptyList() }

        val nextPageNumber = if (page.isEmpty()) {
            null
        } else {
            pageNumber + 1
        }

        LoadResult.Page(
            data = page,
            prevKey = null, // Only paging forward.
            nextKey = nextPageNumber
        )
    } catch (e: Exception) {
        LoadResult.Error(throwable = e)
    }


    private fun List<DummyData>.sort(order: OrderMode): List<DummyData> = when (order) {
        OrderMode.ASC -> sortedBy { it.id }
        OrderMode.DESC -> sortedByDescending { it.id }
    }
}


