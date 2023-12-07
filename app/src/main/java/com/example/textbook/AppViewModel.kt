package com.example.textbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.textbook.database.Repository
import com.example.textbook.paging.TextbookPagingSource

class AppViewModel : ViewModel() {
    private var allSource: TextbookPagingSource? = null
    private var favoriteSource: TextbookPagingSource? = null
    val favoriteFlow = Pager(
        PagingConfig(pageSize = App.PAGE_SIZE)
    ) {
        favoriteSource = TextbookPagingSource { Repository.loadFavoriteTextBooks(it) }
        favoriteSource!!
    }.flow
        .cachedIn(viewModelScope)

    val allFlow = Pager(
        PagingConfig(pageSize = App.PAGE_SIZE)
    ) {
        allSource = TextbookPagingSource { Repository.loadTextbooks(it) }
        allSource!!
    }.flow
        .cachedIn(viewModelScope)

    fun reloadFavorite() {
        favoriteSource?.invalidate()
    }

    fun reloadAll() {
        allSource?.invalidate()
    }
}