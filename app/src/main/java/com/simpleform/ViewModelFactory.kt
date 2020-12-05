package com.simpleform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simpleform.data.Repository
import com.simpleform.ui.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                Repository(),
                Schedulers.io(),
                AndroidSchedulers.mainThread()
            ) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}