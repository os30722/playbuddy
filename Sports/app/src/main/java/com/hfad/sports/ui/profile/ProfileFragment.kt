package com.hfad.sports.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.hfad.sports.R
import com.hfad.sports.databinding.FragmentProfileBinding
import com.hfad.sports.util.TokenToolkit
import com.hfad.sports.util.viewBinding
import com.hfad.sports.vo.Resource
import com.hfad.sports.vo.UserPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var binding by viewBinding<FragmentProfileBinding>()
    private val viewModel: ProfileViewModel by viewModels()

    private var userId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.profile_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.friend_request) {
            findNavController().navigate(R.id.action_profile_to_friend_request)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundleUserPage: UserPage? = requireArguments().getParcelable("user")
        userId = bundleUserPage?.userId ?: TokenToolkit.getUid()

        viewModel.loadProfile(userId!!)

        if(userId == TokenToolkit.getUid()){
            setHasOptionsMenu(true)
        } else {
            binding.friendButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.friendRequest(userId!!).collect { resp ->
                        when (resp) {
                            is Resource.Success -> {
                                changeUi()
                            }
                            is Resource.Error -> {
                                Log.d("debug64", resp.error.message.toString())
                                Toast.makeText(requireContext(), "Connection Error", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                }
            }
        }

        binding.friends.setOnClickListener{
            val bundle = bundleOf(
                "userId" to userId
            )
            findNavController().navigate(R.id.action_profile_to_user_friends, bundle)
        }

        initSubscriber()
    }

    private fun changeUi() {
        if (viewModel.friendStatus.value == null) {
            viewModel.friendStatus.value = "request";
        }
    }


    private fun initSubscriber() {
        viewModel.profileState.observe(viewLifecycleOwner, { resp ->
            when (resp) {

                is Resource.Success -> {
                    binding.profile = resp.data
                }

                is Resource.Error -> {
                    Log.d("debug64", resp.error.toString())
                    Toast.makeText(requireContext(), "Connection Error", Toast.LENGTH_LONG).show()
                }
            }

        })


    }

}