package com.hfad.sports.ui.host

import android.app.Application
import android.location.*
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.hfad.sports.repository.EventRepository
import com.hfad.sports.util.DateTimeFormat
import com.hfad.sports.vo.Event
import com.hfad.sports.vo.ResponseHolder
import com.hfad.sports.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class   HostGameViewModel
    @Inject constructor(
        private val repository: EventRepository,
        application: Application
    ): AndroidViewModel(application){

    private var _state: MutableLiveData<Resource<ResponseHolder>> = MutableLiveData()
    val state: LiveData<Resource<ResponseHolder>> get() = _state


    //Location
    private val _locationSelected: MutableLiveData<LatLng> = MutableLiveData()
    val location:LiveData<LatLng> get() = _locationSelected

    var sportSelected: String = ""
    var eventDate: String = ""
    val startTime = ObservableField<String>()
    val endTime = ObservableField<String>()
    var totalPlayers: String = ""
    val searchLocation = ObservableField<String>()
    var instruction: String = ""


    fun onHostEvent() {
        viewModelScope.launch {
            val startTime = DateTimeFormat.timeFormatUTC("hh : mm aa", startTime.get().toString())
            val endTime = DateTimeFormat.timeFormatUTC("hh : mm aa", endTime.get().toString())
            val eventDate = DateTimeFormat.dateServerFormat(DateTimeFormat.AppDateFormat, eventDate)
            val event = Event(sportSelected, eventDate, startTime, endTime,
                instruction, location.value!!, searchLocation.get().toString() , totalPlayers.toInt()
            )
            repository.hostEvent(event).collect {
                _state.value = it
            }
        }
    }

    fun geoLocate(){

        val geoCoder = Geocoder(getApplication())
        var list: List<Address> = ArrayList()
        Log.d("debug64",searchLocation.get().toString())
        try{
            list = geoCoder.getFromLocationName(searchLocation.get().toString(), 10)

        } catch(e: IOException){
            Log.d("debug64","geoLocation: IOException: " + e.message.toString())
        }

        if(list.size > 0){

            val address = list.get(0)
            _locationSelected.value = LatLng(address.latitude, address.longitude)
        }

    }

    fun getAddress(latLng: LatLng) {
        _locationSelected.value = latLng

        val geoEncoder = Geocoder(getApplication())
        var list: List<Address> = ArrayList()

        try{
            list = geoEncoder.getFromLocation(latLng.latitude, latLng.longitude, 1 )

        } catch(e: IOException){
            Log.d("debug64","geoLocation: IOException: " + e.message.toString())
        }
        val address = list[0]
        searchLocation.set(address.getAddressLine(0))

    }


}