package com.hfad.sports.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import com.hfad.sports.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InboxViewModel
    @Inject constructor(
            private val repository: MessageRepository
    ) : ViewModel() {

        @ExperimentalPagingApi
        val inbox = repository.getInbox().cachedIn(viewModelScope)
}