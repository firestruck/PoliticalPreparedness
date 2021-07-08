package com.example.android.politicalpreparedness.ui.election

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.PoliticalPreparednessRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val repository: PoliticalPreparednessRepository,
    private val id: Int,
    private val division: Division
) : ViewModel() {

    var sAddress = MutableLiveData<String>()
    private val _isElectionSaved :MutableLiveData<Boolean> = MutableLiveData(false)


    //DONE: Add live data to hold voter info

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    //DONE: Add var and methods to populate voter info
   fun getVoterInformation() {
        viewModelScope.launch {
            val voterInfoResult = repository.getVoterInfo(division.state
                .plus(" ")
                .plus(division.country),
               id)
            _voterInfo.value = voterInfoResult
            val address = getAddress(voterInfoResult.state?.firstOrNull()?.electionAdministrationBody?.correspondenceAddress)
            sAddress.value = address

        }
    }

    fun electionStatus(): LiveData<Boolean>{
        viewModelScope.launch(Dispatchers.IO) {
            val getElectionFromDb = repository.getElection(id)
            _isElectionSaved.postValue(getElectionFromDb)
        }
        return _isElectionSaved
    }

    //DONE: Add var and methods to support loading URLs
    private val _eventUrl = MutableLiveData<String?>()
    val eventUrl: LiveData<String?>
        get() = _eventUrl



    private val _ballotUrl = MutableLiveData<String?>()
    val ballotUrl: LiveData<String?>
        get() = _ballotUrl

    fun votingClickListener() {
        _eventUrl.value =
            _voterInfo.value?.state?.firstOrNull()?.electionAdministrationBody?.votingLocationFinderUrl
    }

    fun ballotInfoClickListener() {
        _eventUrl.value =
            _voterInfo.value?.state?.firstOrNull()?.electionAdministrationBody?.ballotInfoUrl
    }



    fun onWebviewCompleted() {
        _eventUrl.value = ""
    }

    fun onBallotNavigated() {
        _ballotUrl.value = ""
    }

    //DONE: Add var and methods to save and remove elections to local database
    //DONE: cont'd -- Populate initial state of save button to reflect proper action based on election saved status
    private fun getAddress(address: Address?): String {
        address?.let {
            return "${it.line1} ${it.line2} ${it.city} ${it.state} ${it.zip}"
        }
        return ""
    }




    fun saveElectionToDb(election: Election){
        viewModelScope.launch {
            if (_isElectionSaved.value!!){
                repository.deleteElection(id)
            }else{
                repository.saveElection(election)
            }
            _isElectionSaved.value = !_isElectionSaved.value!!
        }
    }






    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}