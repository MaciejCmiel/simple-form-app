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
                .zipWith(mainRepository.saveFilledForm(filledForm))
                { _, _ -> true }
                .subscribeOn(processScheduler)
                .observeOn(androidScheduler)
                .subscribe({

                    Timber.d("Updated successfully!")

                    isUpdating.postValue(false)
                    _updateFinishedSuccessfully.postValue(true)

                }, { throwable ->

                    Timber.d("Update failed!")

                    isUpdating.postValue(false)
                    _updateFinishedSuccessfully.postValue(false)
                    Timber.d(throwable)
                })
        )

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}