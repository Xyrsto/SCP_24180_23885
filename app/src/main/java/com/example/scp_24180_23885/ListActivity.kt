package com.example.scp_24180_23885

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Classe que representa a atividade de listagem de cores.
 *
 * Esta atividade exibe uma lista de cores associadas a um usuário e permite remoção e edição.
 *
 * @property currentUserId Identificador do usuário corrente.
 * @property currentUser Nome do usuário corrente.
 * @property backLabel Elemento de interface do tipo TextView associado à label "scpRegisterClickable".
 * @property adapter Adaptador personalizado para a lista de cores.
 * @property colorItems Lista de itens de cores associadas ao usuário.
 */
class ListActivity : AppCompatActivity(), CustomAdapter.OnItemClickListener {
    private lateinit var currentUserId: String
    private lateinit var currentUser: String
    private lateinit var backLabel: TextView
    private lateinit var adapter: CustomAdapter
    val colorItems = mutableListOf<ColorItem>() // Create a list of ColorItems

    /**
     * Método chamado quando a atividade é criada.
     *
     * @param savedInstanceState Dados fornecidos se a atividade está sendo recriada a partir de um estado anterior.
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        currentUserId = intent.getStringExtra("userId").toString()
        currentUser = intent.getStringExtra("userName").toString()

        backLabel = findViewById(R.id.scpRegisterClickable)
        backLabel.setOnClickListener() {
            val intent = Intent(this@ListActivity, MainActivity::class.java)
            intent.putExtra("loggedUser", currentUser)
            startActivity(intent)
        }

        val listView = findViewById<ListView>(R.id.simple_listview)

        adapter = CustomAdapter(this, colorItems, this)
        listView.adapter = adapter

        val api = Retrofit.Builder().baseUrl("https://teste-final-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(retrofitInterface::class.java)

        val body = JsonObject().apply {
            addProperty("owner", intent.getStringExtra("userId").toString())
        }

        api.getUserColors(body).enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val jsonArray = response.body()

                    jsonArray?.forEach { jsonElement ->
                        val jsonObject = jsonElement.asJsonObject
                        val color = jsonObject["color"].asString
                        val colorName = jsonObject["name"].asString

                        colorItems.add(ColorItem(colorName, color)) // Populate colorItems
                    }

                    adapter.notifyDataSetChanged() // Update the adapter with new data
                } else {
                    Log.e("0000", "failed: ${response.message()}")
                    // Handle failure - response.message() contains the error message
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("0000", "error: ${t.message}")
                // Handle failure - t.message contains the failure message
            }
        })

    }

    /**
     * Método chamado quando o botão de remoção é clicado.
     *
     * @param colorItem Item de cor associado ao botão de remoção clicado.
     */
    override fun onRemoveButtonClick(colorItem: ColorItem) {
        removeColor(colorItem)
    }

    /**
     * Método chamado quando o botão de edição é clicado.
     *
     * @param colorItem Item de cor associado ao botão de edição clicado.
     * @param editedText Texto editado a ser atualizado.
     */
    override fun onEditButtonClick(colorItem: ColorItem, editedText: String) {
        updateColorName(colorItem, editedText)
    }

    /**
     * Método para atualizar o nome de uma cor.
     *
     * @param colorItem Item de cor a ser atualizado.
     * @param editedText Novo texto editado.
     */
    private fun updateColorName(colorItem: ColorItem, editedText: String) {
        val api = Retrofit.Builder().baseUrl("https://teste-final-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(retrofitInterface::class.java)

        val requestBody = JsonObject().apply {
            addProperty("owner", currentUserId)
            addProperty("color", colorItem.color)
            addProperty("newName", editedText)
        }

        api.updateColorName(requestBody).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    // Update the colorItem in your list with the new name
                    colorItem.name = editedText
                    // Notify the adapter that the dataset has changed
                    adapter.notifyDataSetChanged()
                    Toast.makeText(
                        applicationContext, "Atualizado com sucesso!", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(applicationContext, "Erro", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Método para remover uma cor.
     *
     * @param colorItem Item de cor a ser removido.
     */
    private fun removeColor(colorItem: ColorItem) {
        val api = Retrofit.Builder().baseUrl("https://teste-final-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(retrofitInterface::class.java)

        val body = JsonObject().apply {
            addProperty("owner", currentUserId)
            addProperty("color", colorItem.color)
        }

        api.removeColor(body).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    colorItems.remove(colorItem)
                    adapter.notifyDataSetChanged()
                } else {
                    // Handle failure
                    Log.e("0000", "failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                // Handle failure
                Log.e("0000", "error: ${t.message}")
            }
        })
    }

}

