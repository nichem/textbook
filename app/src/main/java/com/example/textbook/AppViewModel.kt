package com.example.textbook

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.textbook.database.Repository
import com.example.textbook.database.Textbook
import com.example.textbook.paging.TextbookPagingSource

class AppViewModel : ViewModel() {
    private var allSource: TextbookPagingSource? = null
    private var favoriteSource: TextbookPagingSource? = null
    private var searchKey = ""
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
        allSource = TextbookPagingSource {
            if (searchKey.isEmpty())
                Repository.loadTextbooks(it)
            else
                Repository.findTextbook(searchKey, it)
        }
        allSource!!
    }.flow
        .cachedIn(viewModelScope)

    fun reloadFavorite() {
        favoriteSource?.invalidate()
    }

    fun reloadAll() {
        allSource?.invalidate()
    }

    fun search(key: String) {
        searchKey = key.trim()
        allSource?.invalidate()
    }

    fun quitSearch() {
        searchKey = ""
        allSource?.invalidate()
    }

    fun isSearchState(): Boolean {
        Log.d("test", "<$searchKey> ${searchKey.isNotBlank()}")
        return searchKey.isNotBlank()
    }

    private var _selectItemLiveData = MutableLiveData<Textbook?>()
    val selectItemLiveData: LiveData<Textbook?>
        get() = _selectItemLiveData

    fun isPreviewState() = selectItemLiveData.value != null

    fun selectItem(textbook: Textbook) {
        _selectItemLiveData.postValue(textbook)
    }

    fun quitSelectItem() {
        _selectItemLiveData.postValue(null)
    }
}