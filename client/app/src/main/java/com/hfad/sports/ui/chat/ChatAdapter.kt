package com.hfad.sports.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hfad.sports.R
import com.hfad.sports.databinding.ChatBubbleBinding
import com.hfad.sports.vo.Conversation


class ChatAdapter() : PagingDataAdapter<Conversation, ChatAdapter.ChatViewHolder>(UIMODEL_COMPARATOR) {

    class ChatViewHolder(val binding: ChatBubbleBinding): RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup): ChatViewHolder {
                val view = ChatBubbleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ChatViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder.create(parent)
    }

    override fun getItemViewType(position: Int): Int {
        return  R.layout.chat_bubble
    }


    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.msg = item
    }

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<Conversation>() {
            override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean =
                oldItem == newItem
        }
    }
}