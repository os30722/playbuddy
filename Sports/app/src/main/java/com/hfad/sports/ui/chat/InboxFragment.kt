package com.hfad.sports.ui.chat

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DividerItemDecoration
import com.hfad.sports.R
import com.hfad.sports.api.UserApi
import com.hfad.sports.databinding.FragmentInboxBinding
import com.hfad.sports.db.InboxUpdate
import com.hfad.sports.db.MessageDatabase
import com.hfad.sports.service.MessageService
import com.hfad.sports.service.MsgEvent
import com.hfad.sports.util.TokenToolkit
import com.hfad.sports.util.viewBinding
import com.hfad.sports.vo.Conversation
import com.hfad.sports.vo.Inbox
import com.hfad.sports.vo.UserPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class InboxFragment : Fragment(), InboxItemClick {

    private var binding by viewBinding<FragmentInboxBinding>()
    private val viewModel: InboxViewModel by viewModels()

    private var mBound = false
    private var msgService: MessageService? = null

    @Inject
    lateinit var msgDatabase: MessageDatabase

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            mBound = true
            msgService = (service as MessageService.MessageBinder).getService()
            msgService!!.onListen(MsgEvent.CHAT_MESSAGE, Conversation::class) { conversation ->
                viewLifecycleOwner.lifecycleScope.launch {
                    msgDatabase.inboxDao().updateInbox(conversation)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
            msgService = null
        }

    }

    override fun onStart() {
        super.onStart()
        // Starting messaging service
        Intent(requireContext(), MessageService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInboxBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initInbox()
    }

    @ExperimentalPagingApi
    private fun initInbox() {
        val recyclerView = binding.inboxList
        val pagingAdapter = InboxAdapter(this)
        recyclerView.adapter = pagingAdapter

        // Adding divider
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.inbox.collectLatest { inbox ->
                pagingAdapter.submitData(inbox)
            }
        }
    }


    override fun onInboxItemClick(inbox: Inbox) {
        val navController = findNavController()
        val bundle = bundleOf(
            "userPage" to inbox.user
        )
        navController.navigate(R.id.action_inbox_to_chat, bundle)
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            msgService?.removeListeners()
            activity?.unbindService(connection)
        }
    }


}