package com.hfad.sports.ui.play

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import com.hfad.sports.R
import com.hfad.sports.databinding.FragmentPlayBinding
import com.hfad.sports.databinding.FragmentScheduleBinding
import com.hfad.sports.ui.host.SportListFragment
import com.hfad.sports.ui.schedule.EventClickListener
import com.hfad.sports.ui.schedule.ScheduleAdapter
import com.hfad.sports.ui.schedule.ScheduleViewModel
import com.hfad.sports.util.viewBinding
import com.hfad.sports.vo.EventPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayFragment : Fragment(), EventClickListener {

    private var  binding by viewBinding<FragmentPlayBinding>()
    private val viewModel: PlayViewModel by viewModels()
    private var pagingAdapter: ScheduleAdapter? = null

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPlayBinding.inflate(inflater, container, false)
        binding.model = viewModel
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.play_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search_player){
            findNavController().navigate(R.id.action_play_to_search)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        initPlay()
    }

    private fun initPlay(){
        val recyclerView = binding.playList
        recyclerView.setHasFixedSize(false)
        pagingAdapter = ScheduleAdapter(this)
        recyclerView.adapter= pagingAdapter

        setFragmentResultListener(SportListFragment.EVENT_SELECTED_KEY){ key, bundle ->
            val event = bundle.getString("event")
            viewModel.sportSelected.set(event)
            recommend(event)
        }

        recommend(viewModel.sportSelected.get())

        binding.filter.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_play_to_sport_list))


    }

    private fun recommend(event: String?){
        searchJob?.cancel()
        searchJob = viewLifecycleOwner.lifecycleScope.launch{
            viewModel.loadRecommendation(event).collectLatest {pagingData ->
                pagingAdapter?.submitData(pagingData)
            }
        }
    }

    override fun OnEventClick(eventPage: EventPage) {
        val navController = findNavController()
        val bundle = bundleOf(
            "eventPage" to eventPage
        )
        navController.navigate(R.id.action_play_to_event_info_nav, bundle)
    }

    override fun onDestroyView() {
        pagingAdapter = null
        super.onDestroyView()
    }

}