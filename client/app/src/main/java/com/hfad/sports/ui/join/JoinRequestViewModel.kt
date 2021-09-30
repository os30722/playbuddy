package com.hfad.sports.ui.join

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hfad.sports.repository.EventRepository
import com.hfad.sports.vo.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinRequestViewModel
    @Inject constructor(
        private val repository: EventRepository
    ): ViewModel() {


    fun loadRequests(eventId: Int): Flow<PagingData<UserPage>> {
        return repository.getJoinRequests(eventId)
    }

}