package com.example.android.politicalpreparedness.ui.election

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.repository.PoliticalPreparednessRepository
import java.lang.IllegalArgumentException

//DONE: Create Factory to generate ElectionViewModel with provided election datasource
class ElectionsViewModelFactory(private val context: Context, private val repository: PoliticalPreparednessRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)){
            return ElectionsViewModel(context, repository) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel class")
    }

}