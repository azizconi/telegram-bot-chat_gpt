package it.skills.itskills.bot.repository

import it.skills.itskills.bot.data.remote.Api
import it.skills.itskills.bot.data.repository.ChatGptRepository
import it.skills.itskills.bot.data.request.chat_gpt.ChatGptRequestModel
import it.skills.itskills.bot.data.response.chat_gpt.ChatGptResponseModel
import it.skills.itskills.bot.utils.Resource
import it.skills.itskills.bot.utils.safeApiCall
import kotlinx.coroutines.flow.Flow

class ChatGptRepositoryImpl(private val api: Api): ChatGptRepository {
    override fun sendMessage(data: ChatGptRequestModel): Flow<Resource<ChatGptResponseModel>> = safeApiCall {
        api.sendMessage(data)
    }

}