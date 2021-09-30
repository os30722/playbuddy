package com.hfad.sports.ui.profile

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.hfad.sports.repository.UserRepository
import com.hfad.sports.vo.Resource
import com.hfad.sports.vo.ResponseHolder
import com.hfad.sports.vo.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject constructor(
        private val repository: UserRepository
    ): ViewModel() {

    private var _profileState: MutableLiveData<Resource<UserProfile>> = MutableLiveData()
    val profileState: LiveData<Resource<UserProfile>> get() = _profileState

    var friendStatus = MutableLiveData<String?>()


    fun loadProfile(userId: Int) {
        viewModelScope.launch{
            repository.getProfile(userId).collect{profile ->
                _profileState.value = profile
                if(profile is Resource.Success){
                    friendStatus.value = profile.data.friendStatus
                }
            }
        }
    }

    suspend fun friendRequest(userId: Int): Flow<Resource<ResponseHolder>>{
        return repository.friendRequest(userId)
    }



}