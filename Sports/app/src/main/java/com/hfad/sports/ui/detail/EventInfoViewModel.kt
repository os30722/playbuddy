package com.hfad.sports.ui.detail

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.google.android.gms.common.api.Response
import com.hfad.sports.network.ApiResponse
import com.hfad.sports.repository.EventRepository
import com.hfad.sports.vo.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventInfoViewModel
   @Inject constructor(
       private val repository: EventRepository
   ) : ViewModel() {


    private var _infoState: MutableLiveData<Resource<EventPage>> = MutableLiveData()
    val infoState: LiveData<Resource<EventPage>> get() = _infoState


    val joinedStatus = MutableLiveData<JoinStatus>()

    fun loadInfo(eventId: Int){
        viewModelScope.launch {
            repository.getEventInfo(eventId).collect{
                _infoState.value = it

                if(it is Resource.Success){
                    joinedStatus.value = it.data.playerJoined!!
                }
            }
        }
    }

    suspend fun onJoin(eventId: Int) : Flow<Resource<ResponseHolder>>{
        return repository.joinGame(eventId)
    }

    suspend fun onLeave(eventId: Int): Flow<Resource<ResponseHolder>>{
       return repository.leaveGame(eventId)
    }

    suspend fun deleteEvent(eventId: Int): Flow<Resource<ResponseHolder>> {
        return repository.deleteEvent(eventId)
    }

}
