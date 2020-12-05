package com.simpleform.data

import com.simpleform.data.model.FormElement
import com.simpleform.data.model.TextType
import com.simpleform.data.model.Type
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


class Repository() {

    /**
     * Method for simulating server response
     *
     * @return Observable list of form elements
     */
    fun getFormElements(): Observable<List<FormElement>> {
        return Observable.just(generateList())
            .delay(3000, TimeUnit.MILLISECONDS)

    }

    fun generateList(): List<FormElement> {
        return arrayListOf(
            FormElement("Numer jednostki", Type.TEXT_FIELD, TextType.PHONE),
            FormElement("Zdjęcie budynku", Type.PICTURE),
            FormElement("Kod pocztowy", Type.TEXT_FIELD, TextType.POSTAL),
            FormElement("Adres", Type.TEXT_FIELD, TextType.TEXT)
        )
    }

}