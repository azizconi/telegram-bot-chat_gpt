package it.skills.itskills.bot.data.repository

import it.skills.itskills.bot.data.request.chat_gpt.ChatGptRequestModel
import it.skills.itskills.bot.data.request.text_to_speech.TextToSpeechRequestModel
import it.skills.itskills.bot.data.response.chat_gpt.ChatGptResponseModel
import it.skills.itskills.bot.utils.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface ChatGptRepository {

    fun sendMessage(data: ChatGptRequestModel): Flow<Resource<ChatGptResponseModel>>
    fun textToSpeech(data: TextToSpeechRequestModel): Flow<Resource<ResponseBody>>
}