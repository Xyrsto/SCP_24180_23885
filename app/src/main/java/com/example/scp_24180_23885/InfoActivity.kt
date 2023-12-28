package com.example.scp_24180_23885

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class InfoActivity : AppCompatActivity(){
    private lateinit var scpLabel: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        scpLabel = findViewById(R.id.scpLabel)

        scpLabel.setOnClickListener(){
            val intent = Intent(this@InfoActivity, MainActivity::class.java)
            startActivity(intent)
        }

    }
}