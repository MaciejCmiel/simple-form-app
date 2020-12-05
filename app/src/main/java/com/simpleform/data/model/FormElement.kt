package com.simpleform.data.model

data class FormElement(
    val name: String?,
    val type: Type?,
    val textType: TextType? = null,
    var response: String = ""
)

