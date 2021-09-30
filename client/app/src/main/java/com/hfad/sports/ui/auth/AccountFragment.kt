package com.hfad.sports.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Observer
import com.hfad.sports.R
import com.hfad.sports.databinding.FragmentAccountBinding
import com.hfad.sports.network.Http
import com.hfad.sports.ui.MainActivity
import com.hfad.sports.util.viewBinding
import com.hfad.sports.vo.Resource

class AccountFragment : Fragment() {

    private var binding by viewBinding<FragmentAccountBinding>()
    private val accountModel: SignUpViewModel by hiltNavGraphViewModels(R.id.sign_up_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment

        binding = FragmentAccountBinding.inflate(inflater, container, false)
        binding.account = accountModel


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSignUp()
    }

    private fun initSignUp(){

        accountModel.state.observe(viewLifecycleOwner, Observer{ res ->
            clearError()
            when(res){
                is Resource.Success -> {
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                    if (requireActivity() is UserAuthorizedListener){
                        (requireActivity() as UserAuthorizedListener).onUserAuthorized(res.data)
                    }
                }


                is Resource.Error -> {
                    Toast.makeText(context,"Connection Error", Toast.LENGTH_LONG).show()
                }

                is Resource.HttpError ->{
                    if (res.statusCode == Http.Duplicate){
                        binding.userEmail.error = "Email Id Already Exists"
                    }
                }
            }
            loadingUi(res is Resource.Loading)

        })

    }

    private fun clearError() {
        binding.userEmail.error = ""
    }

    private fun loadingUi(enable: Boolean){
        binding.progressBar.isVisible = enable
        binding.signUpButton.visibility = if (enable) View.INVISIBLE else View.VISIBLE
    }



}