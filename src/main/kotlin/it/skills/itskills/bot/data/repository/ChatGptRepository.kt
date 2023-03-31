package it.skills.itskills.bot.data.repository

import it.skills.itskills.bot.data.request.chat_gpt.ChatGptRequestModel
import it.skills.itskills.bot.data.response.chat_gpt.ChatGptResponseModel
import it.skills.itskills.bot.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ChatGptRepository {

    fun sendMessage(data: ChatGptRequestModel): Flow<Resource<ChatGptResponseModel>>
}