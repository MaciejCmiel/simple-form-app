package com.simpleform.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simpleform.data.Repository
import com.simpleform.data.model.FormElement
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainViewModel(private val mainRepository: Repository) : ViewModel() {

    private val elements = MutableLiveData<List<FormElement>>()
    private val compositeDisposable = CompositeDisposable()

    init {
        getFormElements()
    }

    private fun getFormElements() {
        compositeDisposable.add(
            mainRepository.getFormElements()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userList ->
                    elements.postValue(userList)
                }, { throwable ->
                    elements.value = null
                    Timber.d(throwable)
                })
        )
    }

    fun getElements(): LiveData<List<FormElement>> {
        return elements
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}