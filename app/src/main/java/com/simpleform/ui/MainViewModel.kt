package com.simpleform.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simpleform.data.Repository
import com.simpleform.data.model.FormElement
import com.simpleform.data.model.TextType
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
    private val _isUpdating = MutableLiveData<Boolean>()
    val isUpdating = _isUpdating

    private val _updateFinishedSuccessfully = MutableLiveData<Boolean>()
    val updateFinishedSuccessfully = _updateFinishedSuccessfully

    private val _invalidPostal = MutableLiveData<Boolean>()
    val invalidPostal = _invalidPostal

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

    /**
     * Validate postal code before saving response
     */
    fun validate(filledForm: List<FormElement>) {

        var isValid = true
        for (formElement in filledForm) {
            if (formElement.textType == TextType.POSTAL) {
                if (!formElement.response.matches(Regex("[0-9]{2}[-][0-9]{3}"))) {
                    isValid = false
                }
            }
        }

        if (isValid) {
            saveAndSendFilledForm(filledForm)
        } else {
            invalidPostal.postValue(true)
        }
    }


    /**
     * Save & send user response
     */
    private fun saveAndSendFilledForm(filledForm: List<FormElement>) {

        _isUpdating.postValue(true)

        compositeDisposable.add(
            mainRepository.sendFilledForm(filledForm)
                .zipWith(mainRepository.saveFilledForm(filledForm))
                { _, _ -> true }
                .subscribeOn(processScheduler)
                .observeOn(androidScheduler)
                .subscribe({

                    Timber.d("Updated successfully!")

                    _isUpdating.postValue(false)
                    _updateFinishedSuccessfully.postValue(true)

                }, { throwable ->

                    Timber.d("Update failed!")

                    _isUpdating.postValue(false)
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