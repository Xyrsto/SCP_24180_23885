package com.example.scp_24180_23885

import com.google.gson.annotations.SerializedName

/**
 * Classe que representa um utilizador na aplicação.
 *
 * @property username Nome de utilizador do utilizador.
 * @property email Endereço de e-mail do utilizador.
 * @property password Senha do utilizador.
 */
data class User(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
