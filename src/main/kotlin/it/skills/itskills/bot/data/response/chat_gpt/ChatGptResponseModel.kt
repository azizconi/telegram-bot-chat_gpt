package it.skills.itskills.bot.data.response.chat_gpt

import com.google.gson.annotations.SerializedName

data class ChatGptResponseModel (

	@SerializedName("id") val id : String,
	@SerializedName("object") val objectData : String,
	@SerializedName("created") val created : Int,
	@SerializedName("model") val model : String,
	@SerializedName("usage") val usage : Usage,
	@SerializedName("choices") val choices : List<Choices>
)