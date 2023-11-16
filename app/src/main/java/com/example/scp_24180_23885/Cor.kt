package com.example.scp_24180_23885

import com.google.gson.annotations.SerializedName

data class Cor(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("color") val color: String,
    @SerializedName("Owner") val owner: String
)