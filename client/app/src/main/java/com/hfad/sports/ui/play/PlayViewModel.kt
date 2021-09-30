package com.hfad.sports.ui.play

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.hfad.sports.repository.EventRepository
import com.hfad.sports.vo.EventPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PlayViewModel
    @Inject constructor(
    private val repository: EventRepository
): ViewModel() {

    val sportSelected = ObservableField<String>()
    var recommendEvents: Flow<PagingData<EventPage>>? = null

    fun loadRecommendation(event: String?): Flow<PagingData<EventPage>> {
        return repository.recommendEvents(event)
    }

}