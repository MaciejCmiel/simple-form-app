package com.simpleform.ui

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.simpleform.R
import com.simpleform.data.model.FormElement
import com.simpleform.data.model.TextType
import com.simpleform.data.model.Type
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.list_element.view.*
import kotlinx.android.synthetic.main.list_element.view.elementName
import kotlinx.android.synthetic.main.list_element.view.frameAlert
import kotlinx.android.synthetic.main.list_element_picture.view.*


class ElementsAdapter(
    private val elements: ArrayList<FormElement>,
    private val viewModel: MainViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(formElement: FormElement) {
            // Enable save only when user is focused on the field
            // prevent overwriting text when recycling view
            var enableSave = false
            itemView.elementName.text = formElement.name
            itemView.frameAlert.visibility = View.INVISIBLE
            itemView.etElementInput.setText(formElement.response)

            itemView.etElementInput.doAfterTextChanged {
                // save user input only when user is focused on field
                if (enableSave) {
                    formElement.response =
                        itemView.etElementInput.text.toString()
                }
            }


            when (formElement.textType) {
                TextType.TEXT -> {
                    itemView.etElementInput.inputType = InputType.TYPE_CLASS_TEXT
                    itemView.etElementInput.setOnFocusChangeListener { _, hasFocus ->
                        // Couldn't simplify it as only expressions allowed here
                        (if (hasFocus) enableSave = true
                        else enableSave = false
                                )
                    }
                }
                TextType.PHONE -> {
                    itemView.etElementInput.inputType = InputType.TYPE_CLASS_PHONE
                    itemView.etElementInput.setOnFocusChangeListener { _, hasFocus ->
                        // Couldn't simplify it as only expressions allowed here
                        (if (hasFocus) enableSave = true
                        else enableSave = false
                                )
                    }
                }
                TextType.POSTAL -> {

                    itemView.etElementInput.setOnFocusChangeListener { _, hasFocus ->
                        (if (hasFocus) {
                            itemView.frameAlert.visibility = View.INVISIBLE
                            enableSave = true
                        } else {
                            // Check out if postal code match pattern
                            if (!itemView.etElementInput.text.matches(Regex("[0-9]{2}[-][0-9]{3}"))) {
                                itemView.frameAlert.visibility = View.VISIBLE
                            }
                            enableSave = false
                        })
                    }

                    itemView.etElementInput.inputType =
                        InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
                }
            }

        }
    }

    class PictureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val psOnClick: PublishSubject<Int> = PublishSubject.create()

        fun bind(formElement: FormElement) {
            itemView.elementName.text = formElement.name

            itemView.ivElementInput
            // save user input
            formElement.response //=

            itemView.ivElementInput.setOnClickListener {
                psOnClick.onNext(adapterPosition)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (elements[position].type) {
            Type.PICTURE -> 0
            else -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> PictureViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_element_picture, parent,
                    false
                )
            )
            else -> DataViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_element, parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int = elements.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                (holder as PictureViewHolder).bind(elements[position])
                viewModel.subscribeToPsOnClick(holder.psOnClick)
            }
            else -> (holder as DataViewHolder).bind(elements[position])
        }
    }

    fun addData(list: List<FormElement>) {
        elements.addAll(list)
    }

}