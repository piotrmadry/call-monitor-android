package com.piotrmadry.callmonitor.item

import com.piotrmadry.callmonitor.adapter.RecyclerViewItem

data class CallItem(
    val id: String,
    val name: String,
    val duration: String
) : RecyclerViewItem