package com.hfad.sports.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.sports.repository.UserRepository
import com.hfad.sports.vo.Resource
import com.hfad.sports.vo.Token
import com.hfad.sports.vo.UserSignUp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel
    @Inject constructor(private val repository: UserRepository): ViewModel() {

    private var _state: MutableLiveData<Resource<Token>> = MutableLiveData()
    val state: LiveData<Resource<Token>> get() = _state

    // SignUp inputs
    var firstName: String = ""
    var lastName: String = ""
    var gender: String = ""
    var dob: String = ""
    var email: String = ""
    var pass: String = ""

    fun onSignUp(){
        viewModelScope.launch {
            val userSignUp = UserSignUp(firstName, lastName, dob, gender, email, pass)
            repository.signUpUser(userSignUp).collect{
                _state.value = it
            }
        }

    }

}