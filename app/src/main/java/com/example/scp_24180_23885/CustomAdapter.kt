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

/**
 * Adaptador personalizado para a exibição de itens de cores numa lista.
 *
 * Este adaptador personalizado estende ArrayAdapter<ColorItem> e é projetado para trabalhar com
 * a classe ColorItem para exibir informações sobre cores, incluindo nome e código de cor.
 *
 * @property context Contexto da aplicação.
 * @property colorItems Lista de itens de cor a serem exibidos na lista.
 * @property onItemClickListener Interface para lidar com cliques nos botões da lista.
 */
class CustomAdapter(
    context: Context,
    private val colorItems: List<ColorItem>,
    private val onItemClickListener: OnItemClickListener
) : ArrayAdapter<ColorItem>(context, R.layout.list_item, colorItems) {

    /**
     * Método para obter a exibição formatada para um item na posição específica na lista.
     *
     * @param position Posição do item na lista.
     * @param convertView A exibição reutilizada, se disponível.
     * @param parent O pai ao qual a exibição será anexada.
     * @return A exibição formatada para o item na posição especificada.
     */
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

        holder.editButton?.setOnClickListener {
            colorItem?.let {
                onItemClickListener.onEditButtonClick(it, holder.editTextArea?.text.toString())
            }
        }

        return view!!
    }


    /**
     * Classe interna para armazenar referências às visualizações dentro de cada item na lista.
     */
    private class ViewHolder {
        var colorNameTextView: TextView? = null
        var checkBox: CheckBox? = null
        var removeButton: Button? = null
        var editButton: Button? = null
        var editTextArea: EditText? = null
    }

    /**
     * Interface para lidar com cliques nos botões dentro dos itens da lista.
     */
    interface OnItemClickListener {
        /**
         * Método chamado quando o botão de remoção é clicado.
         *
         * @param colorItem O item de cor associado ao botão de remoção clicado.
         */
        fun onRemoveButtonClick(colorItem: ColorItem)

        /**
         * Método chamado quando o botão de edição é clicado.
         *
         * @param colorItem O item de cor associado ao botão de edição clicado.
         * @param editedText O texto editado no campo de edição associado ao item.
         */
        fun onEditButtonClick(colorItem: ColorItem, toString: String)
    }
}

/**
 * Classe de dados para representar informações sobre uma cor na lista.
 *
 * @property name Nome da cor.
 * @property color Código de cor representado em formato hexadecimal.
 */
class ColorItem(
    var name: String,
    val color: String

)