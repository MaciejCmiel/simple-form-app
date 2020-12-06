package com.simpleform.ui

import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simpleform.data.Repository
import com.simpleform.data.model.FormElement
import com.simpleform.data.model.TextType
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.io.ByteArrayOutputStream

class MainViewModel(
    private val mainRepository: Repository,
    private val processScheduler: Scheduler,
    private val androidScheduler: Scheduler
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _elements = MutableLiveData<List<FormElement>>()
    val elements = _elements

    private val _isUpdating = MutableLiveData<Boolean>()
    val isUpdating = _isUpdating

    private val _updateFinishedSuccessfully = MutableLiveData<Boolean>()
    val updateFinishedSuccessfully = _updateFinishedSuccessfully

    private val _invalidPostal = MutableLiveData<Boolean>()
    val invalidPostal = _invalidPostal

    private val _openGallery = MutableLiveData<Pair<Int, Boolean>>()
    val openGallery = _openGallery

    private val _pictureUpdated = MutableLiveData<Boolean>()
    val pictureUpdated = _pictureUpdated

    init {
        getFormElements()
    }

    private fun getFormElements() {
        compositeDisposable.add(
            mainRepository.getFormElements()
                .subscribeOn(processScheduler)
                .observeOn(androidScheduler)
                .subscribe({ elementsList ->
                    _elements.postValue(elementsList)
                }, { throwable ->
                    _elements.value = null
                    Timber.d(throwable)
                })
        )
    }

    /**
     * Validate postal code before saving response
     */
    fun validate() {

        Timber.d("Data: ${elements.value}")

        val filledForm = elements.value
        if (filledForm.isNullOrEmpty()) {
            invalidPostal.postValue(true)
            return
        }
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

    // Just didn't have better solution that time
    fun subscribeToPsOnClick(psOnClick: PublishSubject<Int>) {
        compositeDisposable.add(
            psOnClick.subscribeOn(processScheduler)
                .observeOn(androidScheduler)
                .subscribe { position ->
                    _openGallery.postValue(position to true)
                }
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    /**
     * Set image in the proper model, function would take bitmap and convert it to Base64
     *
     * as it's only simulation, function just pass Uri to the model to display it in the recyclerView
     *
     * @param data Uri of picked photo from gallery
     *
     *
     */
    fun setImage(data: Uri?) {

        val itemPosition = openGallery.value?.first
        if (itemPosition != null) {
//            elements.value?.get(itemPosition)?.response = encodeBitmap(data)
            elements.value?.get(itemPosition)?.response = data.toString()
            _pictureUpdated.postValue(true)
        }

        Timber.d("Image position: ${openGallery.value}")
    }

    /**
     * Replace Bitmap with Base64.
     *
     * @param imageBitmap image to transform
     * @return image in Base64
     */
    fun encodeBitmap(imageBitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }
}