package com.hfad.sports.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hfad.sports.repository.MessageRepository
import com.hfad.sports.util.TokenToolkit
import com.hfad.sports.vo.Conversation
import com.hfad.sports.vo.EventPage
import com.hfad.sports.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: MessageRepository
): ViewModel() {

    @ExperimentalPagingApi
    var conversation: Flow<PagingData<Conversation>>? = null

    @ExperimentalPagingApi
    fun fetchConversation(recipientId: Int){
        conversation = repository.getConversations(TokenToolkit.getUid(), recipientId).cachedIn(viewModelScope)
    }


}