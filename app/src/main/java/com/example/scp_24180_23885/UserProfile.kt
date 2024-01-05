package com.example.scp_24180_23885

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Classe que representa a atividade do perfil do utilizador.
 * Permite a visualização e edição das informações do utilizador.
 */
class UserProfile : AppCompatActivity() {
    private lateinit var listarLabel: TextView
    private lateinit var backlabel: TextView
    private lateinit var editUsernameTextView: EditText
    private lateinit var editPasswordTextView: EditText
    private lateinit var editEmailTextView: EditText
    private lateinit var editUsernameButton: Button
    private lateinit var editPasswordButton: Button
    private lateinit var editEmailButton: Button
    private lateinit var currentUserId: String
    private var currentUser: String? = null
    private lateinit var logoutButton: Button
    var api = Retrofit.Builder().baseUrl("https://teste-final-production.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create()).build()
        .create(retrofitInterface::class.java)

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        currentUserId = intent.getStringExtra("userId").toString()
        currentUser = intent.getStringExtra("userName").toString()

        listarLabel = findViewById<TextView>(R.id.listarLabel)
        backlabel = findViewById<TextView>(R.id.backlabel)
        editUsernameTextView = findViewById(R.id.editUsernameTextview)
        editEmailTextView = findViewById(R.id.editEmailTextview)
        editPasswordTextView = findViewById(R.id.editTextTextPassword3)
        editUsernameButton = findViewById(R.id.editUsernameButton)
        editEmailButton = findViewById(R.id.editEmailButton)
        editPasswordButton = findViewById(R.id.editPasswordButton)
        logoutButton = findViewById(R.id.logoutButton)

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

        editUsernameButton.setOnClickListener(){
            val newUsername = editUsernameTextView.text.toString()

            if(newUsername.isNullOrEmpty() || newUsername == currentUser || currentUser.equals("null")){
                Toast.makeText(
                    applicationContext, "Insira um username válido", Toast.LENGTH_SHORT
                ).show()
            }
            else{
                val requestBody = JsonObject().apply {
                    addProperty("username", currentUser)
                    addProperty("newUsername", newUsername)
                }

                val call = api.updateUsername(requestBody)

                call.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            currentUser = newUsername
                            editUsernameTextView.setText("")
                                Toast.makeText(
                                    applicationContext, "Atualizado com sucesso!", Toast.LENGTH_SHORT
                                ).show()
                        }
                        else{
                            Toast.makeText(
                                applicationContext, "Insira um username válido", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(
                            applicationContext, "Insira um username válido", Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }

        editEmailButton.setOnClickListener(){
            val newEmail = editEmailTextView.text.toString()

            if(newEmail.isNullOrEmpty() || currentUser.equals("null")){
                Toast.makeText(
                    applicationContext, "Insira um email válido", Toast.LENGTH_SHORT
                ).show()
            }
            else{
                val requestBody = JsonObject().apply {
                    addProperty("username", currentUser)
                    addProperty("newEmail", newEmail)
                }

                val call = api.updateEmail(requestBody)

                call.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            editEmailTextView.setText("")
                            Toast.makeText(
                                applicationContext, "Atualizado com sucesso!", Toast.LENGTH_SHORT
                            ).show()
                        }
                        else{
                            Toast.makeText(
                                applicationContext, "Insira um email válido", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(
                            applicationContext, "Insira um email válido", Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }

        editPasswordButton.setOnClickListener(){
            val newPassword = editPasswordTextView.text.toString()

            if(newPassword.isNullOrEmpty() || currentUser.equals("null")){
                Toast.makeText(
                    applicationContext, "Insira uma password válida", Toast.LENGTH_SHORT
                ).show()
            }
            else{
                val requestBody = JsonObject().apply {
                    addProperty("username", currentUser)
                    addProperty("newPassword", newPassword)
                }

                val call = api.updatePassword(requestBody)

                call.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            editPasswordTextView.setText("")
                            Toast.makeText(
                                applicationContext, "Atualizado com sucesso!", Toast.LENGTH_SHORT
                            ).show()

                        }
                        else{
                            Toast.makeText(
                                applicationContext, "Insira uma password válida", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(
                            applicationContext, "Insira uma password válida", Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }

        logoutButton.setOnClickListener() {
            currentUser = null // Set currentUser to null
            val intent = Intent(this@UserProfile, MainActivity::class.java)
            intent.putExtra("loggedUser", currentUser)
            startActivity(intent)
        }

    }
}