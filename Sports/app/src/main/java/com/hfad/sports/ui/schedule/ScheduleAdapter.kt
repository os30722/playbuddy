package com.hfad.sports.ui.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hfad.sports.R
import com.hfad.sports.databinding.EventPageBinding
import com.hfad.sports.vo.EventPage

interface EventClickListener {
    fun OnEventClick(eventPage: EventPage)
}


class ScheduleAdapter(val callback: EventClickListener?) : PagingDataAdapter<EventPage, ScheduleAdapter.EventViewHolder>(UIMODEL_COMPARATOR) {

    class EventViewHolder(val binding: EventPageBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup): EventViewHolder {
                val view = EventPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return EventViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder.create(parent)
    }

    override fun getItemViewType(position: Int): Int {
        return  R.layout.event_page
    }


    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.event = item
        holder.binding.callback = callback
    }

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<EventPage>() {
            override fun areItemsTheSame(oldItem: EventPage, newItem: EventPage): Boolean {
                return oldItem.eventId == newItem.eventId
            }

            override fun areContentsTheSame(oldItem: EventPage, newItem: EventPage): Boolean =
                oldItem == newItem
        }
    }
}