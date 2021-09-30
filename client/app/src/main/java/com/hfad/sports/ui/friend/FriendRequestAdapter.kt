package com.hfad.sports.ui.friend

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hfad.sports.api.UserApi
import com.hfad.sports.databinding.FriendRequestBinding
import com.hfad.sports.vo.UserPage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FriendRequestAdapter(): PagingDataAdapter<UserPage, FriendRequestAdapter.FriendRequestHolder>(UIMODEL_COMPARATOR){

    class FriendRequestHolder(val binding: FriendRequestBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup): FriendRequestHolder {
                val view = FriendRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return FriendRequestHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestHolder {
        return FriendRequestHolder.create(parent)
    }

    override fun onBindViewHolder(holder: FriendRequestHolder, position: Int) {
        val item = getItem(position)
        holder.binding.user = item
        holder.binding.acceptButton.setOnClickListener{
            val api = UserApi.create()
            val call = api.acceptFriend(item!!.userId)
            call.enqueue(object: Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    holder.binding.message.visibility = View.VISIBLE
                    holder.binding.acceptButton.visibility = View.INVISIBLE
                    holder.binding.removeButton.visibility = View.INVISIBLE
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }

        holder.binding.removeButton.setOnClickListener {
            val api = UserApi.create()
            val call = api.removeFriend(item!!.userId)
            call.enqueue(object: Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("debug64","Sucess")

                    refresh()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
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