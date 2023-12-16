package com.example.scp_24180_23885

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserProfile : AppCompatActivity() {
    private lateinit var listarLabel: TextView
    private lateinit var backlabel: TextView
    private lateinit var currentUserId: String
    private lateinit var currentUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val api = Retrofit.Builder().baseUrl("https://teste-final-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(retrofitInterface::class.java)

        currentUserId = intent.getStringExtra("userId").toString()
        currentUser = intent.getStringExtra("userName").toString()

        listarLabel = findViewById<TextView>(R.id.listarLabel)
        backlabel = findViewById<TextView>(R.id.backlabel)

        backlabel.setOnClickListener() {
            val intent = Intent(this@UserProfile, MainActivity::class.java)
            intent.putExtra("loggedUser", currentUser)
            startActivity(intent)
        }

        listarLabel.setOnClickListener() {
            val intent = Intent(this@UserProfile, ListActivity::class.java)
            intent.putExtra("userId", currentUserId)
            intent.putExtra("userName", currentUser)
            startActivity(intent)
        }
    }
}