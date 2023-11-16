package com.example.scp_24180_23885

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private lateinit var registerLabel: TextView
private lateinit var loginButton: Button
private lateinit var mainScreenLabel: TextView
private lateinit var usernameLoginField: EditText
private lateinit var passwordLoginField: EditText

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        registerLabel = findViewById(R.id.registerLabel)
        loginButton = findViewById(R.id.loginButton)
        mainScreenLabel = findViewById(R.id.scpRegisterClickable)
        usernameLoginField = findViewById(R.id.usernameLoginField)
        passwordLoginField = findViewById(R.id.passwordLoginField)


        registerLabel.setOnClickListener(){
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener(){
            login();
        }

        mainScreenLabel.setOnClickListener(){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    //TODO -> Código relacionado ao login na API(acho que é com retrofit)
    private fun login(){
        var username = usernameLoginField.text;
        var password = passwordLoginField.text;

        val api = Retrofit.Builder()
            .baseUrl("https://teste-final-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(retrofitInterface::class.java)

        val body = JsonObject().apply {
            addProperty("username", username.toString())
            addProperty("password", password.toString())
        }

        api.login(body).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    // Handle success - response.body() contains the successful response body
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("loggedUser", username.toString());
                    startActivity(intent)
                } else {
                    when(response.code()){
                        401 -> Toast.makeText(applicationContext, "Credenciais incorretas", Toast.LENGTH_SHORT).show()
                        404 -> Toast.makeText(applicationContext, "Utilizador não existe", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("0000", "error: ${t.message}")
                // Handle failure - t.message contains the failure message
            }
        })
    }
}