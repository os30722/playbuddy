package com.hfad.sports.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.hfad.sports.R
import com.hfad.sports.databinding.FragmentLoginBinding
import com.hfad.sports.ui.MainActivity
import com.hfad.sports.util.viewBinding
import com.hfad.sports.vo.Resource
import com.hfad.sports.vo.Token
import dagger.hilt.android.AndroidEntryPoint

interface UserAuthorizedListener {
    fun onUserAuthorized(tokens: Token)
}

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var binding by viewBinding<FragmentLoginBinding>()
    private val loginModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.login = loginModel



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLogin()
        subscribeObserver()
    }

    private fun initLogin(){
        binding.createAccount.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_login_to_signUpGraph))
    }

    private fun subscribeObserver() {
        loginModel.state.observe(viewLifecycleOwner, {
            loadingUi(it is Resource.Loading)
            clearError()

            when(it){
                is Resource.Success -> {
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                    if (requireActivity() is UserAuthorizedListener){
                        (requireActivity() as UserAuthorizedListener).onUserAuthorized(it.data)
                    }
                }

                is Resource.InputError ->{
                    displayError(it.message)
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(),"Connection Error", Toast.LENGTH_LONG).show()
                }

                is Resource.HttpError ->{
                    if(it.statusCode == 401){
                        displayError("* Incorrect Email and Password")
                    }
                    else{
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                    }
                }

            }
        })

    }

    private fun clearError(){
        binding.loginEmail.error = ""
        binding.loginPassword.error = ""
    }

    private fun displayError(message: String){
        binding.loginEmail.error = " "
        binding.loginEmail.getChildAt(1).visibility = View.GONE
        binding.loginPassword.error = message
    }


    private fun loadingUi(enable: Boolean){
        binding.progressBar.visibility = View.VISIBLE
        binding.loginButton.visibility = if (enable) View.INVISIBLE else View.VISIBLE
    }



}