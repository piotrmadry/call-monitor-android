package com.piotrmadry.callmonitor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piotrmadry.callmonitor.adapter.RecyclerViewItem
import com.piotrmadry.callmonitor.di.qualifier.IO
import com.piotrmadry.callmonitor.item.CallItem
import com.piotrmadry.callmonitor.item.ServerInfoItem
import com.piotrmadry.callmonitor.usecase.CallHistoryUseCase
import com.piotrmadry.callmonitor.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @IO private val dispatcherIo: CoroutineDispatcher,
    private val networkUtils: NetworkUtils,
    private val callHistory: CallHistoryUseCase
) : ViewModel() {

    private val _items = MutableLiveData<List<RecyclerViewItem>>(listOf())
    val items: LiveData<List<RecyclerViewItem>> = _items

    private val _progress = MutableLiveData(true)
    val progress: LiveData<Boolean> = _progress

    fun getData() {
        _progress.value = _items.value?.isEmpty() ?: true

        viewModelScope.launch {
            val callItems = withContext(dispatcherIo) {
                callHistory.getLogCompact().map {
                    CallItem(
                        id = it.id,
                        name = it.contactName,
                        duration = it.durationInSeconds
                    )
                }
            }
            _progress.value = false
            _items.value = createServerInfoItem() + callItems
        }
    }

    private fun createServerInfoItem() =
        listOf(
            ServerInfoItem(
                ipAddress = networkUtils.getLocalIPAddressWithPort() ?: "Local IP address not found"
            )
        )
}