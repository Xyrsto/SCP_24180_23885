package com.example.scp_24180_23885

import com.google.gson.annotations.SerializedName

/**
 * Classe de dados para representar informações sobre uma cor na aplicação SCP.
 *
 * Esta classe é usada para mapear os dados de uma cor, incluindo o nome, descrição, código de cor
 * e proprietário, utilizando as anotações [SerializedName] do Gson.
 *
 * @property name Nome da cor.
 * @property description Descrição da cor.
 * @property color Código de cor representado em formato hexadecimal.
 * @property owner Proprietário da cor.
 */
data class Cor(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("color") val color: String,
    @SerializedName("Owner") val owner: String
)