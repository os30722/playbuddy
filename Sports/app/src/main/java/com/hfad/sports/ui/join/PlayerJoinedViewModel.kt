package com.hfad.sports.ui.join

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.hfad.sports.repository.EventRepository
import com.hfad.sports.vo.UserPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PlayerJoinedViewModel
    @Inject constructor(
        private val repository: EventRepository
    ): ViewModel() {

    var query: String? = null

    fun loadPlayersJoined(eventId: Int): Flow<PagingData<UserPage>> {
        return repository.getJoinedUsers(eventId, query)
    }

}