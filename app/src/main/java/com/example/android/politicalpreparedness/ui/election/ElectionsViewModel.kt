package com.example.android.politicalpreparedness.ui.election

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.PoliticalPreparednessRepository
import kotlinx.coroutines.launch

//DONE: Construct ViewModel and provide election datasource
class ElectionsViewModel(context : Context, private val repository: PoliticalPreparednessRepository) : ViewModel() {

    //DONE: Create live data val for upcoming elections

    private val _upcomingElection = MutableLiveData<List<Election>>()
    val upcomingElection: LiveData<List<Election>>
        get() = _upcomingElection



    //DONE: Create live data val for saved elections
    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections : LiveData<List<Election>>
        get() = _savedElections


    //DONE: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    private fun getUpcomingElections(){
        viewModelScope.launch {
            val upcoming = repository.getUpcominElectionOnline()
            _upcomingElection.value = upcoming
        }
    }
    private fun getSavedElections(){
        viewModelScope.launch {
            val saved = repository.getSavedElections()

            _savedElections.value = saved
        }
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info
    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo




    fun electionClickListener(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun onNavigationCompleted() {
        _navigateToVoterInfo.value = null
    }

    fun refreshAdapters(){
        getUpcomingElections()
        getSavedElections()
    }



}