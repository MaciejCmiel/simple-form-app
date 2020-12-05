package com.simpleform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simpleform.data.Repository
import com.simpleform.ui.MainViewModel

class ViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(Repository()) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}