package com.simpleform.ui

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simpleform.data.model.FormElement
import com.simpleform.data.model.TextType
import com.simpleform.data.model.Type
import com.simpleform.databinding.ListElementBinding
import kotlinx.android.synthetic.main.list_element.view.*


class ElementsAdapter(
    private val elements: ArrayList<FormElement>
) : RecyclerView.Adapter<ElementsAdapter.DataViewHolder>() {


    class DataViewHolder(private val binding: ListElementBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(formElement: FormElement) {

            binding.formElement = formElement

            itemView.etElementInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //blank
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    //blank;
                }

                override fun afterTextChanged(s: Editable) {

                }
            })

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
            ListElementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
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