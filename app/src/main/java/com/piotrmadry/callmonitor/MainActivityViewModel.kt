package com.piotrmadry.callmonitor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    @IO private val dispatcherIo: CoroutineDispatcher,
    private val networkHelper: NetworkHelper,
    private val callHistory: CallHistory
) : ViewModel() {

    private val _items = MutableLiveData<List<RecyclerViewItem>>()
    val items: LiveData<List<RecyclerViewItem>> = _items

    fun getData() {
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
            _items.value = createServerInfoItem() + callItems
        }
    }

    private fun createServerInfoItem() =
        listOf(
            ServerInfoItem(
                ipAddress = networkHelper.getLocalIPAddressWithPort() ?: "Local IP address not found"
            )
        )
}