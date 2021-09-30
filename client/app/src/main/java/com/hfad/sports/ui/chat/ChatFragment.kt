package com.hfad.sports.ui.chat

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.RecyclerView
import androidx.room.withTransaction
import com.hfad.sports.databinding.FragmentChatBinding
import com.hfad.sports.db.MessageDatabase
import com.hfad.sports.service.MessageService
import com.hfad.sports.service.MsgEvent
import com.hfad.sports.util.TokenToolkit
import com.hfad.sports.util.viewBinding
import com.hfad.sports.vo.Conversation
import com.hfad.sports.vo.SendMessage
import com.hfad.sports.vo.UserPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var binding by viewBinding<FragmentChatBinding>()
    private val viewModel: ChatViewModel by viewModels()

    private var mBound = false
    private var msgService: MessageService? = null
    private var senderId: Int? = null

    @Inject
    lateinit var msgDatabase: MessageDatabase

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            mBound = true
            msgService = (service as MessageService.MessageBinder).getService()
            readMessage()
            msgService!!.onListen(MsgEvent.CHAT_MESSAGE, Conversation::class) { conversation ->
                viewLifecycleOwner.lifecycleScope.launch {
                    msgDatabase.withTransaction {
                        msgDatabase.chatsDao().addConversation(conversation)
                        msgDatabase.inboxDao().updateInbox(conversation)
                    }

                    readMessage()

                }

            }

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
            msgService = null
        }
    }

    private fun readMessage() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (TokenToolkit.getUid() != senderId) {
                msgService?.sendMessage(
                    MsgEvent.SEEN_MESSAGE,
                    senderId!!
                )
                msgDatabase.inboxDao().readAll(senderId!!)
            }
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
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initChat()

    }

    @ExperimentalPagingApi
    private fun initChat() {
        val userPage: UserPage? = requireArguments().getParcelable("userPage")
        if (userPage != null) {
            senderId = userPage.userId

            (requireActivity() as AppCompatActivity).supportActionBar?.title =
                userPage.firstName + ' ' + userPage.lastName
            viewModel.fetchConversation(userPage.userId)


            // To send message
            binding.sendButton.setOnClickListener {
                val editText = binding.messageInput.editText
                val text = editText!!.text.toString()
                if (text.replace(" ", "").isNotEmpty()) {
                    msgService?.sendMessage(
                        MsgEvent.CHAT_MESSAGE,
                        SendMessage(userPage.userId, text)
                    )
                    editText.setText("")
                }
            }
        }

        val recyclerView = binding.conversations
        val pagingAdapter = ChatAdapter()
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = pagingAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.conversation?.collectLatest { pagingData ->
                pagingAdapter.submitData(pagingData)
            }

        }


        // To scroll to the bottom of the list when new conversation arrives
        pagingAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    if (!recyclerView.canScrollVertically(1))
                        recyclerView.scrollToPosition(0)
                }
            }
        })


    }


    override fun onDestroy() {
        super.onDestroy()
        if (mBound) {
            msgService?.removeListeners()
            activity?.unbindService(connection)
        }
    }
}