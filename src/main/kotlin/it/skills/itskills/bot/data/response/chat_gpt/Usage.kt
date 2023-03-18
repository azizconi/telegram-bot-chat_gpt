package it.skills.itskills.bot.data.response.chat_gpt

import com.google.gson.annotations.SerializedName

data class Usage (

	@SerializedName("prompt_tokens") val prompt_tokens : Int,
	@SerializedName("completion_tokens") val completion_tokens : Int,
	@SerializedName("total_tokens") val total_tokens : Int
)