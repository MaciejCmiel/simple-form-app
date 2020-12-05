package com.simpleform.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simpleform.data.Repository
import com.simpleform.data.model.FormElement
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class MainViewModel(
    private val mainRepository: Repository,
    private val processScheduler: Scheduler,
    private val androidScheduler: Scheduler
) : ViewModel() {

    private val elements = MutableLiveData<List<FormElement>>()
    private val compositeDisposable = CompositeDisposable()
    private val isUpdating = MutableLiveData<Boolean>()
    private val _updateFinishedSuccessfully = MutableLiveData<Boolean>()
    val updateFinishedSuccessfully = _updateFinishedSuccessfully

    init {
        getFormElements()
    }

    private fun getFormElements() {
        compositeDisposable.add(
            mainRepository.getFormElements()
                .subscribeOn(processScheduler)
                .observeOn(androidScheduler)
                .subscribe({ elementsList ->
                    elements.postValue(elementsList)
                }, { throwable ->
                    elements.value = null
                    Timber.d(throwable)
                })
        )
    }

    fun getElements(): LiveData<List<FormElement>> {
        return elements
    }

    fun isUpdating(): LiveData<Boolean> {
        return isUpdating
    }

    fun sendFilledForm(filledForm: List<FormElement>?) {

        isUpdating.postValue(true)

        if (filledForm == null) {
            return
        }

        compositeDisposable.add(
            mainRepository.sendFilledForm(filledForm)
                .subscribeOn(processScheduler)
                .observeOn(androidScheduler)
                .subscribe({
                    //TODO show success

                    _updateFinishedSuccessfully.postValue(true)
                    isUpdating.postValue(false)

                }, { throwable ->

                    _updateFinishedSuccessfully.postValue(false)
                    isUpdating.postValue(false)
                    Timber.d(throwable)
                })
        )

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}