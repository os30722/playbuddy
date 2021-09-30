package com.hfad.sports.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hfad.sports.repository.UserRepository
import com.hfad.sports.vo.UserPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class SearchViewModel
    @Inject constructor(
        private val repository: UserRepository
    ): ViewModel() {

    var query: String = ""


    fun onSearch(): Flow<PagingData<UserPage>> {
        return repository.searchUsers(query).cachedIn(viewModelScope)
    }
}