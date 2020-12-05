package com.simpleform.ui

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simpleform.R
import com.simpleform.data.model.FormElement
import com.simpleform.data.model.TextType
import com.simpleform.data.model.Type
import kotlinx.android.synthetic.main.list_element.view.*


class ElementsAdapter(
    private val elements: ArrayList<FormElement>
) : RecyclerView.Adapter<ElementsAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(formElement: FormElement) {
            itemView.elementName.text = formElement.name

            when (formElement.type) {
                Type.TEXT_FIELD -> {

                    when (formElement.textType) {
                        TextType.TEXT -> {
                            itemView.etElementInput.inputType = InputType.TYPE_CLASS_PHONE
                        }
                        TextType.PHONE -> {
                            itemView.etElementInput.inputType = InputType.TYPE_CLASS_PHONE
                        }
                        TextType.POSTAL -> {

                            itemView.etElementInput.setOnFocusChangeListener { _, hasFocus ->
                                (
                                        if (hasFocus) {
                                            itemView.frameAlert.visibility = View.INVISIBLE

                                        } else {
                                            // Check out if postal code match pattern
                                            if (!itemView.etElementInput.text.matches(Regex("[0-9]{2}[-][0-9]{3}"))) {
                                                itemView.frameAlert.visibility = View.VISIBLE
                                            }
                                        }
                                        )
                            }

                            itemView.etElementInput.inputType =
                                InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
                        }
                    }
                }
                Type.PICTURE -> {
                    // TODO add gallery picker

                }
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_element, parent,
                false
            )
        )

    override fun getItemCount(): Int = elements.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(elements[position])

    fun addData(list: List<FormElement>) {
        elements.addAll(list)
    }

}