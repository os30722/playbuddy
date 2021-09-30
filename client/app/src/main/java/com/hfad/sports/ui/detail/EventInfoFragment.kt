package com.hfad.sports.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.hfad.sports.R
import com.hfad.sports.databinding.FragmentEventInfoBinding

import com.hfad.sports.util.TokenToolkit

import com.hfad.sports.util.viewBinding
import com.hfad.sports.vo.EventPage
import com.hfad.sports.vo.JoinStatus
import com.hfad.sports.vo.Resource
import com.hfad.sports.vo.UserPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_event_info.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventInfoFragment : Fragment() {

    private var  binding by viewBinding<FragmentEventInfoBinding>()
    private val viewModel: EventInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEventInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    private fun showDeleteDialog(eventId: Int){
        val builder = AlertDialog.Builder(requireActivity())
        builder.apply {
            setTitle("Delete Event")
            setMessage("Are you sure, you want to delete the event ?")
            setPositiveButton("Delete Event"){ dialog, id ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deleteEvent(eventId).collect()
                }
                findNavController().popBackStack()

            }
            setNegativeButton("Cancel"){ dialog, id ->

            }
        }.create().show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eventPage: EventPage? = requireArguments().getParcelable("eventPage")

        if (eventPage != null) {

            if(eventPage.host.userId == TokenToolkit.getUid()){
                binding.toolbar.inflateMenu(R.menu.event_menu)
                binding.toolbar.setOnMenuItemClickListener{ menu ->
                    if(menu.itemId == R.id.delete_event){
                        showDeleteDialog(eventPage.eventId)
                    }

                    true
                }
            }


            binding.eventPage = eventPage
            binding.userPage = eventPage.host
            binding.viewModel = viewModel

            viewModel.loadInfo(eventPage.eventId)


            initInfo(eventPage, eventPage.host)
        }

    }

    private fun changeUi(resp: Resource<*>){
        when(resp) {
            is Resource.Success -> {
                when(viewModel.joinedStatus.value){
                    JoinStatus.NOT_JOINED -> viewModel.joinedStatus.value = JoinStatus.REQUEST
                    JoinStatus.JOINED -> viewModel.joinedStatus.value = JoinStatus.NOT_JOINED

                }
            }
            is Resource.Error -> Toast.makeText(requireContext(),"Connection Error", Toast.LENGTH_LONG).show()

        }
    }

    private fun initInfo(eventPage: EventPage, userPage: UserPage) {

        binding.joinBut.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                when (viewModel.joinedStatus.value) {
                    JoinStatus.NOT_JOINED -> {
                        viewModel.onJoin(eventPage.eventId).collect { resp ->
                            changeUi(resp)
                        }
                    }
                    JoinStatus.JOINED -> {
                        viewModel.onLeave(eventPage.eventId).collect {resp ->
                            changeUi(resp)
                        }
                    }
                }
            }
        }

        viewModel.infoState.observe(viewLifecycleOwner, { resp ->
            when (resp) {
                is Resource.Success -> {
                    binding.eventPage = resp.data
                    changeUi(resp)
                }
            }

        })

        binding.requestButton.setOnClickListener {
            val bundle = bundleOf(
                "eventId" to eventPage.eventId
            )
            findNavController().navigate(R.id.action_event_info_to_join_request, bundle)
        }

        binding.chatButton.setOnClickListener {
            val bundle = bundleOf(
                "userPage" to eventPage.host
            )
            findNavController().navigate(R.id.action_event_info_to_chat, bundle)
        }

        binding.noPlayer.setOnClickListener{
            val bundle = bundleOf(
                "eventId" to eventPage.eventId,
                "hostId" to userPage.userId
            )
            findNavController().navigate(R.id.action_event_info_to_players_joined, bundle)
        }
    }


}