package com.hfad.sports.ui.join

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hfad.sports.R
import com.hfad.sports.api.EventApi
import com.hfad.sports.databinding.FragmentPlayerJoinedBottomSheetBinding
import com.hfad.sports.util.viewBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlayerJoinedBottomSheetFragment :BottomSheetDialogFragment() {

    private var binding by viewBinding<FragmentPlayerJoinedBottomSheetBinding>()
    private val viewModel:PlayerJoinedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPlayerJoinedBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBottomSheet()
    }

    private fun initBottomSheet(){
        val playerId = requireArguments().getInt("playerId")
        val eventId = requireArguments().getInt("eventId")

        Log.d("debug64","eventId => "+ eventId)

        binding.removePlayer.setOnClickListener{
            EventApi.create().removeFromGame(eventId, playerId).enqueue(object: Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("debug64", "Sucess")
                    findNavController().popBackStack()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }

    }





}