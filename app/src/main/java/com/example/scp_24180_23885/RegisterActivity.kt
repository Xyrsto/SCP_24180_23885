package com.example.scp_24180_23885

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

private lateinit var registerButton: Button
private lateinit var mainScreenLabel: TextView
private lateinit var emailRegister: EditText
private lateinit var usernameRegister: EditText
private lateinit var passwordRegister: EditText

/**
 * Classe que representa a atividade de registo de utilizador.
 *
 * Esta atividade permite que um novo utilizador se registe fornecendo as informações necessárias.
 *
 * @property registerButton Elemento de interface do tipo Button associado ao botão de registo.
 * @property mainScreenLabel Elemento de interface do tipo TextView associado à label "scpRegisterClickable".
 * @property emailRegister Elemento de interface do tipo EditText associado ao campo de email.
 * @property usernameRegister Elemento de interface do tipo EditText associado ao campo de nome de utilizador.
 * @property passwordRegister Elemento de interface do tipo EditText associado ao campo de senha.
 */
class RegisterActivity : AppCompatActivity() {

    /**
     * Método chamado quando a atividade é criada.
     *
     * @param savedInstanceState Dados fornecidos se a atividade está sendo recriada a partir de um estado anterior.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerButton = findViewById(R.id.registerButton)
        mainScreenLabel = findViewById(R.id.scpRegisterClickable)
        emailRegister = findViewById(R.id.emailRegisterField)
        usernameRegister = findViewById(R.id.usernameRegisterField)
        passwordRegister = findViewById(R.id.passwordRegisterField)

        registerButton.setOnClickListener(){
            register()
        }

        mainScreenLabel.setOnClickListener() {
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Método responsável por realizar a operação de registo de utilizador.
     */
    fun register(){
        val api = Retrofit.Builder()
            .baseUrl("https://teste-final-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(retrofitInterface::class.java)

        val body = JsonObject().apply {
            addProperty("username", usernameRegister.text.toString())
            addProperty("email", emailRegister.text.toString())
            addProperty("password", passwordRegister.text.toString())
        }

        api.addUser(body).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    // Handle success - response.body() contains the successful response body
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Registo efetuado com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    when(response.code()){
                        400 -> Toast.makeText(applicationContext, "Erro ao criar utilizador", Toast.LENGTH_SHORT).show()
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
