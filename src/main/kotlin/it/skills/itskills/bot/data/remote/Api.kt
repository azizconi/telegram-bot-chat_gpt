package it.skills.itskills.bot.data.remote

import it.skills.itskills.bot.data.request.chat_gpt.ChatGptRequestModel
import it.skills.itskills.bot.data.response.chat_gpt.ChatGptResponseModel
import it.skills.itskills.bot.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface Api {


    @POST("v1/chat/completions")
    suspend fun sendMessage(
        @Body data: ChatGptRequestModel,
//        @Header("Authorization") value: String = "Bearer ${Constants.CHAT_GPT_API_TOKEN}",
//        @Header("Content-Type") contentType: String = "application/json"
    ): Response<ChatGptResponseModel>

}