package com.example.scp_24180_23885

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.scp_24180_23885.Cor
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Interface que define os endpoints da API utilizando Retrofit para comunicação com o servidor.
 */
interface retrofitInterface {

    /**
     * Obtém a lista de cores disponíveis.
     *
     * @return Objeto Call contendo a lista de cores.
     */
    @GET("/getColor")
    fun getColor(): Call<List<Cor>>

    /**
     * Obtém o ID de um utilizador com base no nome de utilizador.
     *
     * @param requestBody Corpo da solicitação contendo o nome de utilizador.
     * @return Objeto Call contendo o ID do utilizador.
     */
    @POST("/getId")
    fun getId(@Body requestBody: JsonObject): Call<JsonObject>

    /**
     * Adiciona uma nova cor à base de dados.
     *
     * @param requestBody Corpo da solicitação contendo as informações da cor a ser adicionada.
     * @return Objeto Call indicando o resultado da operação.
     */
    @POST("/addColor")
    fun addColor(@Body requestBody: JsonObject): Call<JsonObject>

    /**
     * Realiza o login do utilizador.
     *
     * @param requestBody Corpo da solicitação contendo as credenciais do utilizador.
     * @return Objeto Call indicando o resultado do login.
     */
    @POST("/login")
    fun login(@Body requestBody: JsonObject): Call<JsonObject>

    /**
     * Adiciona um novo utilizador à base de dados.
     *
     * @param requestBody Corpo da solicitação contendo as informações do novo utilizador.
     * @return Objeto Call indicando o resultado do registo.
     */
    @POST("/addUser")
    fun addUser(@Body requestBody: JsonObject): Call<JsonObject>

    /**
     * Obtém a lista de cores associadas a um utilizador.
     *
     * @param requestBody Corpo da solicitação contendo o ID do utilizador.
     * @return Objeto Call contendo a lista de cores do utilizador.
     */
    @POST("/getUserColors")
    fun getUserColors(@Body requestBody: JsonObject): Call<JsonArray>

    /**
     * Remove uma cor da base de dados.
     *
     * @param requestBody Corpo da solicitação contendo as informações da cor a ser removida.
     * @return Objeto Call indicando o resultado da remoção da cor.
     */
    @POST("/removeColor")
    fun removeColor(@Body requestBody: JsonObject): Call<JsonObject>

    /**
     * Atualiza o nome de uma cor na base de dados.
     *
     * @param requestBody Corpo da solicitação contendo as informações da cor a ser atualizada.
     * @return Objeto Call indicando o resultado da atualização do nome da cor.
     */
    @POST("/updateColorName")
    fun updateColorName(@Body requestBody: JsonObject): Call<JsonObject>

    /**
     * Atualiza o nome de utilizador na base de dados.
     *
     * @param requestBody Corpo da solicitação contendo as informações do utilizador a ser atualizado.
     * @return Objeto Call indicando o resultado da atualização do nome de utilizador.
     */
    @POST("/updateUsername")
    fun updateUsername(@Body requestBody: JsonObject): Call<JsonObject>

    /**
     * Atualiza o email do utilizador na base de dados.
     *
     * @param requestBody Corpo da solicitação contendo as informações do utilizador a ser atualizado.
     * @return Objeto Call indicando o resultado da atualização do email do utilizador.
     */
    @POST("/updateEmail")
    fun updateEmail(@Body requestBody: JsonObject): Call<JsonObject>

    /**
     * Atualiza a senha do utilizador na base de dados.
     *
     * @param requestBody Corpo da solicitação contendo as informações do utilizador a ser atualizado.
     * @return Objeto Call indicando o resultado da atualização da senha do utilizador.
     */
    @POST("/updatePassword")
    fun updatePassword(@Body requestBody: JsonObject): Call<JsonObject>
}
