package com.simpleform.ui

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simpleform.R
import com.simpleform.data.model.FormElement
import kotlinx.android.synthetic.main.list_element.view.*

class ElementsAdapter(
    private val elements: ArrayList<FormElement>
) : RecyclerView.Adapter<ElementsAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(formElement: FormElement) {
            itemView.elementName.text = formElement.name

            // TODO set proper input type and hint based on element type
            itemView.elementInput.inputType = InputType.TYPE_CLASS_TEXT
            itemView.elementInput.hint = ""


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