package com.piotrmadry.callmonitor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piotrmadry.callmonitor.databinding.CallItemBinding
import com.piotrmadry.callmonitor.databinding.ServerInfoItemBinding

interface RecyclerViewItem

data class ServerInfoItem(val ipAddress: String) : RecyclerViewItem

data class CallItem(val id: String, val name: String, val duration: String) : RecyclerViewItem

class CallMonitorRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ServerInfoViewHolder(val binding: ServerInfoItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    class CallViewHolder(val binding: CallItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var dataSet = listOf<RecyclerViewItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<RecyclerViewItem>) {
        dataSet = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> ServerInfoViewHolder(
                ServerInfoItemBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
            )
            else -> CallViewHolder(
                CallItemBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataSet[position]) {
            is ServerInfoItem -> 0
            is CallItem -> 1
            else -> -1
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val context = viewHolder.itemView.context
        when (val item = dataSet[position]) {
            is ServerInfoItem -> {
                (viewHolder as ServerInfoViewHolder).binding.ipAddress.text =
                    context.getString(R.string.server_info_item_title, item.ipAddress)
            }
            is CallItem ->
                (viewHolder as CallViewHolder).binding.apply {
                    name.text = context.getString(R.string.call_item_contact_name, item.name)
                    duration.text = context.getString(R.string.call_item_duration, item.duration)
                }
        }
    }

    override fun getItemCount() = dataSet.size
}