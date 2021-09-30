package com.hfad.sports.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.sports.repository.UserRepository
import com.hfad.sports.vo.Resource
import com.hfad.sports.vo.Token
import com.hfad.sports.vo.UserLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
   @Inject constructor(private val repository: UserRepository): ViewModel() {

    private var _state: MutableLiveData<Resource<Token>> = MutableLiveData()
    val state: LiveData<Resource<Token>> get() = _state

    var email: String = ""
    var pass: String = ""


    fun onLogin(){
        viewModelScope.launch{
            if (email.length == 0 || pass.length == 0) {
                _state.value = Resource.inputError("* Please Enter Email and Password")
                return@launch
            }
            repository.loginUser(UserLogin(email, pass)).collect {
                _state.value = it
            }
        }
    }
}