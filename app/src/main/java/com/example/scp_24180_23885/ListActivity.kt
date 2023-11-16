package com.example.scp_24180_23885

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
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

class ListActivity: AppCompatActivity() {
    private lateinit var currentUserId : String
    private lateinit var backLabel : TextView
    val colorItems = mutableListOf<ColorItem>() // Create a list of ColorItems

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        backLabel = findViewById(R.id.scpRegisterClickable)

        backLabel.setOnClickListener(){
            val intent = Intent(this@ListActivity, MainActivity::class.java)
            intent.putExtra("loggedUser", currentUserId)
            startActivity(intent)
        }

        val listView = findViewById<ListView>(R.id.simple_listview)

        val adapter = CustomAdapter(this, colorItems)
        listView.adapter = adapter

        val api = Retrofit.Builder()
            .baseUrl("https://teste-final-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
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


}