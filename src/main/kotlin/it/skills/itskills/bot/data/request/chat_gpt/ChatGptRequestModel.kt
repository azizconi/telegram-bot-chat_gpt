package it.skills.itskills.bot.data.request.chat_gpt

import com.google.gson.annotations.SerializedName

data class ChatGptRequestModel (

    @SerializedName("model") val model : String,
    @SerializedName("messages") val messages : List<Message>,
    @SerializedName("temperature") val temperature : Double
)