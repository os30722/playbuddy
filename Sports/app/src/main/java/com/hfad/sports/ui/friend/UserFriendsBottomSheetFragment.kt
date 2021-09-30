package com.hfad.sports.ui.friend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hfad.sports.R
import com.hfad.sports.api.UserApi
import com.hfad.sports.databinding.FragmentUserFriendsBottomSheetBinding
import com.hfad.sports.util.viewBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserFriendsBottomSheetFragment : BottomSheetDialogFragment() {

    private var binding by viewBinding<FragmentUserFriendsBottomSheetBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserFriendsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBottomSheet()
    }

    private fun initBottomSheet(){
        val userId = requireArguments().getInt("userId")
        if(userId != -1){
            binding.unfriend.setOnClickListener {
                UserApi.create().removeFriend(userId).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        findNavController().popBackStack()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }


}