package com.example.android.politicalpreparedness.ui.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.PoliticalPreparednessRepository
import com.example.android.politicalpreparedness.ui.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(val repository: PoliticalPreparednessRepository): ViewModel() {

    //DONE: Establish live data for representatives and address

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    init{
        // init an empty address (with Alabama state to match the spinner default starting value)
        // to avoid a crash in case user clicks on find my representatives
        // without adding any address info
        _address.value = Address("", "", "", "Alabama", "")
    }



    //DONE: Create function to fetch representatives from API from a provided address

    fun getRepresentatives(address: Address){
        val line1 = address.line1
        val line2 = address.line2
        val state = address.state
        val city = address.city
        val zip = address.zip

        val completeAddress = "$line1 $line2, $city, $state, $zip"
        getRepresentatives(completeAddress)
    }


    fun getRepresentatives(address: String) {
        viewModelScope.launch {
            val (offices, officials) = repository.getRepresentatives(address)
            _representatives.value = offices.flatMap {
                    office -> office.getRepresentatives(officials)
            }
        }
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //DONE: Create function get address from geo location

    fun getAddress(address: Address){
        _address.value = address
    }

    //TODO: Create function to get address from individual fields

}
