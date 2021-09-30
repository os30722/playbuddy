package com.hfad.sports.ui.schedule

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import com.hfad.sports.R
import com.hfad.sports.databinding.FragmentScheduleBinding
import com.hfad.sports.db.EventDatabase
import com.hfad.sports.util.viewBinding
import com.hfad.sports.vo.EventPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleFragment : Fragment(), EventClickListener {

    private var  binding by viewBinding<FragmentScheduleBinding>()
    private val viewModel: ScheduleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentScheduleBinding.inflate(inflater, container, false)

        return binding.root
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSchedule()
    }

    @ExperimentalPagingApi
    private fun initSchedule(){
        binding.hostGame.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.host_game_nav))

        val recyclerView = binding.scheduleList
        val pagingAdapter = ScheduleAdapter(this)
        recyclerView.adapter = pagingAdapter

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.schedule.collectLatest {schedule ->
                pagingAdapter.submitData(schedule)
            }
        }

    }

    override fun OnEventClick(eventPage: EventPage) {
        val navController = findNavController()
        val bundle = bundleOf(
            "eventPage" to eventPage
        )
        navController.navigate(R.id.action_schedule_to_event_info, bundle)
    }


}