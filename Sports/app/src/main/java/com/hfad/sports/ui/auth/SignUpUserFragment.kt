package com.hfad.sports.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.Navigation
import com.hfad.sports.R
import com.hfad.sports.databinding.FragmentSignupUserBinding
import com.hfad.sports.util.viewBinding


class SignUpUserFragment : Fragment() {

    private var binding by viewBinding<FragmentSignupUserBinding>()
    private val signUpModel: SignUpViewModel by hiltNavGraphViewModels(R.id.sign_up_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignupUserBinding.inflate(inflater, container, false)
        binding.form = signUpModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSignUp()
    }

    fun initSignUp(){
        binding.nextButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_register_to_account))
    }


}