package com.hfad.sports.ui.join

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.hfad.sports.R
import com.hfad.sports.databinding.FragmentPlayerJoinedBinding
import com.hfad.sports.util.viewBinding
import com.hfad.sports.vo.UserPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PlayersJoined : Fragment(), PlayerJoinedAdapter.OnItemClick {

    private var binding by viewBinding<FragmentPlayerJoinedBinding>()
    private val viewModel:PlayerJoinedViewModel by viewModels()
    private var eventId:Int? = null
    private var pagingAdapter: PlayerJoinedAdapter? = null

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPlayerJoinedBinding.inflate(inflater, container, false)
        binding.model = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initJoined()
    }

    private fun loadData() {
        searchJob?.cancel()
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadPlayersJoined(eventId!!).collectLatest { pagingData ->
                pagingAdapter?.submitData(pagingData)
            }
        }
    }

    private fun initJoined(){

        val recyclerView = binding.playersList
        eventId = requireArguments().getInt("eventId")
        val hostId: Int = requireArguments().getInt("hostId")
        pagingAdapter = PlayerJoinedAdapter(hostId, this)
        recyclerView.adapter = pagingAdapter

        // Adding divider
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)

        binding.searchBar.searchQuery.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val im = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                im.hideSoftInputFromWindow(requireView().windowToken, 0)
                loadData()
            }
            false
        }

        loadData()


    }

    override fun onMenuClick(userPage: UserPage, view: View) {
        val bundle = bundleOf(
            "playerId"  to userPage.userId,
            "eventId" to eventId,
        )

        findNavController().navigate(R.id.action_player_joined_to_player_joined_bottom_sheet, bundle)

    }

    override fun onDestroyView() {
        pagingAdapter = null
        super.onDestroyView()
    }

}

