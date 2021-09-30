package com.hfad.sports.ui.join

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hfad.sports.api.EventApi
import com.hfad.sports.databinding.JoinRequestBinding
import com.hfad.sports.vo.UserPage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinRequestAdapter(private val eventId: Int): PagingDataAdapter<UserPage, JoinRequestAdapter.JoinRequestHolder>(UIMODEL_COMPARATOR){

    class JoinRequestHolder(val binding: JoinRequestBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup): JoinRequestHolder {
                val view = JoinRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return JoinRequestHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JoinRequestHolder{
        return JoinRequestHolder.create(parent)
    }

    override fun onBindViewHolder(holder: JoinRequestHolder, position: Int) {
        val item = getItem(position)
        holder.binding.user = item
        holder.binding.acceptButton.setOnClickListener {
            val api = EventApi.create()
            val call = api.acceptRequest(eventId,  item!!.userId)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    holder.binding.message.text = "has Joined the game."
                    holder.binding.message.visibility = View.VISIBLE
                    holder.binding.acceptButton.visibility = View.INVISIBLE
                    holder.binding.removeButton.visibility = View.INVISIBLE
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {

                }

            })
        }

        holder.binding.removeButton.setOnClickListener {
            val api = EventApi.create()
            val call = api.removeFromGame(eventId, item!!.userId)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    holder.binding.message.text = "has been removed from the game."
                    holder.binding.message.visibility = View.VISIBLE
                    holder.binding.acceptButton.visibility = View.INVISIBLE
                    holder.binding.removeButton.visibility = View.INVISIBLE
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {

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