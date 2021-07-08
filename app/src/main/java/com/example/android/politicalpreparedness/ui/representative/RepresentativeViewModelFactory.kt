package com.example.android.politicalpreparedness.ui.representative

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.repository.PoliticalPreparednessRepository
import java.lang.IllegalArgumentException

class RepresentativeViewModelFactory(private val repository: PoliticalPreparednessRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java)){
            return RepresentativeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel class")
    }

}