package com.example.scp_24180_23885

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.scp_24180_23885.Cor
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface retrofitInterface {
    @GET("/getColor")
    fun getColor(): Call<List<Cor>>
    // Define other API endpoints here

    @POST("/addColor")
    fun addColor(@Body requestBody: JsonObject): Call<JsonObject>
}
