package com.example.scp_24180_23885

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

private lateinit var registerLabel: TextView
private lateinit var loginButton: Button
private lateinit var mainScreenLabel: TextView

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        registerLabel = findViewById(R.id.registerLabel)
        loginButton = findViewById(R.id.loginButton)
        mainScreenLabel = findViewById(R.id.scpRegisterClickable)


        registerLabel.setOnClickListener(){
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener(){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }

        mainScreenLabel.setOnClickListener(){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    //TODO -> Código relacionado ao login na API(acho que é com retrofit)
}