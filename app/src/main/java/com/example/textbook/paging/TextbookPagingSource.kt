package com.example.textbook.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.textbook.App.Companion.PAGE_SIZE
import com.example.textbook.database.LocalDao
import com.example.textbook.database.Repository
import com.example.textbook.database.Textbook

class TextbookPagingSource(private val loadPages: suspend (Int) -> List<Textbook>) :
    PagingSource<Int, Textbook>() {
    override fun getRefreshKey(state: PagingState<Int, Textbook>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Textbook> {
        try {
            val key = params.key ?: 0
            val textbooks = loadPages(key)
            return LoadResult.Page<Int, Textbook>(
                textbooks,
                if (key == 0) null else key - 1,
                key + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}