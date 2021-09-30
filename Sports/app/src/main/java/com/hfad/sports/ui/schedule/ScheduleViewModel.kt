package com.hfad.sports.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import com.hfad.sports.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ScheduleViewModel
    @Inject constructor(
        private val repository: EventRepository
        ): ViewModel() {

    @ExperimentalPagingApi
    val schedule = repository.getUserSchedule().cachedIn(viewModelScope)
}