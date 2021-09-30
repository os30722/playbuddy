package com.hfad.sports.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hfad.sports.R
import com.hfad.sports.databinding.InboxBinding
import com.hfad.sports.vo.Inbox

interface InboxItemClick {
    fun onInboxItemClick(inbox: Inbox)
}

class InboxAdapter(private val callback: InboxItemClick?): PagingDataAdapter<Inbox, InboxAdapter.InboxViewHolder>(UIMODEL_COMPARATOR) {
    
    class InboxViewHolder(val binding: InboxBinding): RecyclerView.ViewHolder(binding.root){
        companion object{
            fun create(parent: ViewGroup): InboxViewHolder {
                val view = InboxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return InboxViewHolder(view)
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InboxAdapter.InboxViewHolder {
        return InboxViewHolder.create(parent)
    }

    
    override fun onBindViewHolder(holder: InboxAdapter.InboxViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.inbox = item
        holder.binding.item.setOnClickListener {
            if(item != null)
                callback?.onInboxItemClick(inbox = item)
        }
    }

   
    override fun getItemViewType(position: Int): Int {
        return  R.layout.inbox
    }

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<Inbox>() {
            override fun areItemsTheSame(oldItem: Inbox, newItem: Inbox): Boolean {
                return oldItem.user.userId == newItem.user.userId
            }

            override fun areContentsTheSame(oldItem: Inbox, newItem: Inbox): Boolean =
                oldItem == newItem
        }
    }


}