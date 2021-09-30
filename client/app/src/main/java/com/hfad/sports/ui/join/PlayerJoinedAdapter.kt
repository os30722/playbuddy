package com.hfad.sports.ui.join

import android.security.KeyChainAliasCallback
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hfad.sports.databinding.UserPageBinding
import com.hfad.sports.vo.UserPage


class PlayerJoinedAdapter(val hostId: Int, val callback: OnItemClick?): PagingDataAdapter<UserPage, PlayerJoinedAdapter.UserViewHolder>(UIMODEL_COMPARATOR){

    interface OnItemClick  {
        fun onMenuClick(userPage: UserPage, v: View)
    }

    class UserViewHolder(val binding: UserPageBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup): UserViewHolder {
                val view = UserPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return UserViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder.create(parent)

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.user = item

        if(item?.userId == hostId){
            holder.binding.apply {
                hostLabel.visibility = View.VISIBLE
                options.visibility = View.INVISIBLE
            }
        } else{
            holder.binding.apply {
                hostLabel.visibility = View.GONE
                options.visibility = View.VISIBLE
            }
        }

        holder.binding.options.setOnClickListener{
            callback?.onMenuClick(item!!, holder.binding.root)
        }
    }


    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<UserPage>() {
            override fun areItemsTheSame(oldItem: UserPage, newItem: UserPage): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: UserPage, newItem: UserPage): Boolean =
                oldItem == newItem
        }
    }


}