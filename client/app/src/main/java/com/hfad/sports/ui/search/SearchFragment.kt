package com.hfad.sports.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
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
import com.hfad.sports.databinding.FragmentSearchBinding
import com.hfad.sports.util.viewBinding
import com.hfad.sports.vo.UserPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchAdapter.ItemClickListener {

    private var  binding by viewBinding<FragmentSearchBinding>()
    private val viewModel: SearchViewModel by viewModels()

    private var searchJob: Job? = null
    private var searchAdapter: SearchAdapter? = SearchAdapter(this)

    private fun search() {
        searchJob?.cancel()
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.onSearch().collectLatest {
                searchAdapter?.submitData(it)

            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.search = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearch()
    }

    private fun initSearch(){
        binding.searchBar.searchQuery.requestFocus()
        val im = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        im.showSoftInput(binding.searchBar.searchQuery, InputMethodManager.SHOW_IMPLICIT)

        binding.searchBar.searchQuery.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                im.hideSoftInputFromWindow(requireView().windowToken, 0)
                search()

            }
            false
        }

        val recyclerView = binding.userList
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = searchAdapter

        // Adding divider
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)


    }

    override fun onItemClick(userPage: UserPage) {
        val bundle = bundleOf(
            "user" to userPage
        )
        findNavController().navigate(R.id.action_search_to_profile_nav, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchAdapter = null
    }


}