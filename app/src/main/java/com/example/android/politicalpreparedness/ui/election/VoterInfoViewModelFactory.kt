package com.example.android.politicalpreparedness.ui.election

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.repository.PoliticalPreparednessRepository
import java.lang.IllegalArgumentException

//DONE: Create Factory to generate VoterInfoViewModel with provided election datasource
class VoterInfoViewModelFactory(
    private val repository: PoliticalPreparednessRepository,
    private val id: Int,
    private val division: Division
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            return VoterInfoViewModel(repository, id, division) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}