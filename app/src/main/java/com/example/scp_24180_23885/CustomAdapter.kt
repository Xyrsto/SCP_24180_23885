package com.example.scp_24180_23885

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView

class CustomAdapter(
    context: Context,
    private val colorItems: List<ColorItem>,
    private val onItemClickListener: OnItemClickListener
) : ArrayAdapter<ColorItem>(context, R.layout.list_item, colorItems) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            holder = ViewHolder()
            holder.colorNameTextView = view.findViewById(R.id.colorName)
            holder.checkBox = view.findViewById(R.id.colorDisplay)
            holder.removeButton = view.findViewById(R.id.removeButton)
            holder.editButton = view.findViewById(R.id.editButton)
            holder.editTextArea = view.findViewById(R.id.editTextArea)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val colorItem = colorItems[position]
        holder.colorNameTextView?.text = colorItem.name
        holder.checkBox?.setBackgroundColor(Color.parseColor(colorItem.color))

        // Set up click listener for the removeButton
        holder.removeButton?.setOnClickListener {
            colorItem?.let { onItemClickListener.onRemoveButtonClick(it) }
        }

        holder.editButton?.setOnClickListener{
            colorItem?.let{
                onItemClickListener.onEditButtonClick(it, holder.editTextArea?.text.toString())
            }
        }

        return view!!
    }

    private class ViewHolder {
        var colorNameTextView: TextView? = null
        var checkBox: CheckBox? = null
        var removeButton: Button? = null
        var editButton: Button? = null
        var editTextArea: EditText? = null
    }

    interface OnItemClickListener {
        fun onRemoveButtonClick(colorItem: ColorItem)
        fun onEditButtonClick(colorItem: ColorItem, toString: String)
    }
}


class ColorItem (
    var name: String,
    val color: String

)