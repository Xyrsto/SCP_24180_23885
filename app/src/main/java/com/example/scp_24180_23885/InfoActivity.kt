package com.example.scp_24180_23885

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

/**
 * Classe que representa a atividade de informações.
 *
 * Esta atividade exibe informações específicas e permite a navegação para a atividade principal.
 *
 * @property currentUserId Identificador do usuário corrente.
 * @property currentUser Nome do usuário corrente.
 * @property scpLabel Elemento de interface do tipo TextView associado à label "scpLabel".
 */
class InfoActivity : AppCompatActivity(){
    private lateinit var currentUserId: String
    private var currentUser: String? = null
    private lateinit var scpLabel: TextView

    /**
     * Método chamado quando a atividade é criada.
     *
     * @param savedInstanceState Dados fornecidos se a atividade está sendo recriada a partir de um estado anterior.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        currentUserId = intent.getStringExtra("userId").toString()
        currentUser = intent.getStringExtra("userName").toString()

        scpLabel = findViewById(R.id.scpLabel)

        scpLabel.setOnClickListener(){
            val intent = Intent(this@InfoActivity, MainActivity::class.java)
            intent.putExtra("loggedUser", currentUser)
            startActivity(intent)
        }
    }
}