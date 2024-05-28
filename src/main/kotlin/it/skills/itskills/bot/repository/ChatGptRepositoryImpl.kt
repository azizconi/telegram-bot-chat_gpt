package it.skills.itskills.bot.repository

import it.skills.itskills.bot.data.remote.Api
import it.skills.itskills.bot.data.repository.ChatGptRepository
import it.skills.itskills.bot.data.request.chat_gpt.ChatGptRequestModel
import it.skills.itskills.bot.data.request.text_to_speech.TextToSpeechRequestModel
import it.skills.itskills.bot.data.response.chat_gpt.ChatGptResponseModel
import it.skills.itskills.bot.utils.Resource
import it.skills.itskills.bot.utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

class ChatGptRepositoryImpl(private val api: Api): ChatGptRepository {
    override fun sendMessage(data: ChatGptRequestModel): Flow<Resource<ChatGptResponseModel>> = safeApiCall {
        api.sendMessage(data)
    }

    override fun textToSpeech(data: TextToSpeechRequestModel): Flow<Resource<ResponseBody>> = safeApiCall {
        api.textToSpeech(data)
    }
}