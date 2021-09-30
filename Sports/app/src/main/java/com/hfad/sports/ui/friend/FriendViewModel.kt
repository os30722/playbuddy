package com.hfad.sports.ui.friend

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.hfad.sports.repository.UserRepository
import com.hfad.sports.vo.UserPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class FriendViewModel
    @Inject constructor(
        private val repository: UserRepository
    )
    : ViewModel() {

    var query: String? = null

    val friendRequests = repository.getFriendRequests()

    fun loadUserFriends(userId: Int): Flow<PagingData<UserPage>> {
        return repository.getUserFriends(userId, query)
    }

}