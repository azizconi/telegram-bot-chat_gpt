package it.skills.itskills.bot.data.response.chat_gpt

import com.google.gson.annotations.SerializedName

data class Choices (

	@SerializedName("message") val message : Message,
	@SerializedName("finish_reason") val finish_reason : String,
	@SerializedName("index") val index : Int
)