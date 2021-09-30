package com.hfad.sports.ui.friend

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
import com.hfad.sports.databinding.FragmentUserFriendsBinding
import com.hfad.sports.util.viewBinding
import com.hfad.sports.vo.UserPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFriendsFragment : Fragment(), UserFriendsAdapter.ItemClickListener {

    private var binding by viewBinding<FragmentUserFriendsBinding>()
    private val viewModel: FriendViewModel by viewModels()
    private var pagingAdapter: UserFriendsAdapter? = null
    private var userId: Int? = null

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserFriendsBinding.inflate(inflater, container, false)
        binding.model = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUserFriends()
    }

    private fun loadData(){
        searchJob?.cancel()
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadUserFriends(userId!!).collectLatest { pagingData ->
                pagingAdapter?.submitData(pagingData)
            }
        }
    }

    private fun initUserFriends(){

        val recyclerView = binding.friendsList
        userId = requireArguments().getInt("userId")
        pagingAdapter = UserFriendsAdapter(this)
        recyclerView.adapter = pagingAdapter

        // Adding divider
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)

        binding.searchBar.searchQuery.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.d("debug64","Search => " + viewModel.query)
                val im = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                im.hideSoftInputFromWindow(requireView().windowToken, 0)
                loadData()
            }
            false
        }

        loadData()
    }

    override fun onMenuClick(userPage: UserPage, v: View) {
        val bundle = bundleOf(
            "userId" to userPage.userId
        )
        findNavController().navigate(R.id.action_user_friends_to_user_friends_bottom_sheet, bundle)
    }

}