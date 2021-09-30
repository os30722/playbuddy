package com.hfad.sports.ui.join

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hfad.sports.databinding.FragmentJoinRequestBinding
import com.hfad.sports.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class JoinRequestFragment : Fragment() {

    private var binding by viewBinding<FragmentJoinRequestBinding>()
    private val viewModel: JoinRequestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentJoinRequestBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }

    private fun initAdapter() {

        val recyclerView = binding.requestList
        val eventId: Int = requireArguments().getInt("eventId")
        val pagingAdapter = JoinRequestAdapter(eventId)
        recyclerView.adapter = pagingAdapter


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadRequests(eventId).collectLatest { pagingData ->
                pagingAdapter.submitData(pagingData)
            }
        }

    }


}