package it.skills.itskills.bot.data.request.chat_gpt

import com.google.gson.annotations.SerializedName

data class Message (

	@SerializedName("role") val role : String,
	@SerializedName("content") val content : String
)