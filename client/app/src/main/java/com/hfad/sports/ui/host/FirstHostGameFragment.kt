package com.hfad.sports.ui.host

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.fragment.app.setFragmentResultListener
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.Navigation
import com.google.android.material.datepicker.*
import com.google.android.material.timepicker.MaterialTimePicker
import com.hfad.sports.R
import com.hfad.sports.databinding.FragmentFirstHostGameBinding
import com.hfad.sports.util.DateTimeFormat
import com.hfad.sports.util.viewBinding
import java.util.*

class FirstHostGameFragment : Fragment() {

    private var binding by viewBinding<FragmentFirstHostGameBinding>()
    private val hostViewModel: HostGameViewModel by hiltNavGraphViewModels(R.id.host_game_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFirstHostGameBinding.inflate(inflater, container, false)
        binding.event = hostViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initHostGame()
    }

    private fun initHostGame() {

        setFragmentResultListener(SportListFragment.EVENT_SELECTED_KEY) { key, bundle ->
            val event = bundle.getString("event")
            if(event != null){
                hostViewModel.sportSelected = event
            }
        }

        binding.selectSport.editText?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.sport_list)
        )

        binding.startTime.editText?.setOnClickListener { v ->
            showTimePicker("Set Start Time", hostViewModel.startTime)
        }

        binding.endTime.editText?.setOnClickListener { v ->
            showTimePicker("Set End Time", hostViewModel.endTime)
        }

        binding.navNext.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_first_host_game_to_second_host_game))

    }

    private fun showTimePicker(title: String, property: ObservableField<String>) {
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText(title)
            .build()
        timePicker.show((requireActivity()).supportFragmentManager, "time_picker")

        timePicker.addOnPositiveButtonClickListener {
            property.set(DateTimeFormat.appTimeFormat("HH mm",
                timePicker.hour.toString() + " " +
                        timePicker.minute.toString()
            ))


        }
    }


}