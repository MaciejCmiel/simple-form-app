package com.simpleform.data

import com.simpleform.data.model.FormElement
import com.simpleform.data.model.TextType
import com.simpleform.data.model.Type
import com.simpleform.data.retrofit.SuccessResponse
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class Repository() {

    /**
     * Method for simulating server response
     *
     * @return Observable list of form elements
     */
    fun getFormElements(): Single<List<FormElement>> {
        return Single.just(generateList())
            .delay(3, TimeUnit.SECONDS)

    }

    fun generateList(): List<FormElement> {
        return arrayListOf(
            FormElement("Numer jednostki", Type.TEXT_FIELD, TextType.PHONE),
            FormElement("Zdjęcie budynku", Type.PICTURE),
            FormElement("Kod pocztowy", Type.TEXT_FIELD, TextType.POSTAL),
            FormElement("Adres", Type.TEXT_FIELD, TextType.TEXT)
        )
    }

    /**
     * Send form to server
     */
    fun sendFilledForm(filledForm: List<FormElement>): Single<SuccessResponse> {
        return Single.just(SuccessResponse())
            .delay(Random.nextLong(3, 6), TimeUnit.SECONDS)
    }

    /**
     * Save form to local database
     *
     * @return row id of the item inserted
     */
    fun saveFilledForm(filledForm: List<FormElement>): Single<Long> {
        return Single.just(Random.nextLong())
            .delay(Random.nextLong(2, 5), TimeUnit.SECONDS)
    }

}