package it.skills.itskills.bot


import it.skills.itskills.bot.data.remote.retrofit.RetrofitInstance
import it.skills.itskills.bot.data.request.chat_gpt.ChatGptRequestModel
import it.skills.itskills.bot.data.request.chat_gpt.Message
import it.skills.itskills.bot.repository.ChatGptRepositoryImpl
import it.skills.itskills.bot.utils.Constants
import it.skills.itskills.bot.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

@Service
class TelegramBot : TelegramLongPollingBot() {
    override fun getBotToken(): String {
        return Constants.TELEGRAM_BOT_TOKEN
    }

    override fun getBotUsername(): String {
        return Constants.TELEGRAM_BOT_USERNAME
    }

    val scope = CoroutineScope(Dispatchers.Default)
    val repository/*: ChatGptRepository*/ = ChatGptRepositoryImpl(RetrofitInstance.api())

    companion object {
        const val TAG = "TelegramBot: "
    }

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()) {

            val message = update.message
            val chatId = message.chatId.toString()

            println("Question: ${message.text}")
//            val responseText = if (message.hasText()) {
//                val messageText = message.text
//                when (messageText) {
//                    "/start" -> "Добро пожаловать!"
//                    else -> "Вы написали: *$messageText*"
//                }
//            } else {
//                "Я понимаю только текст"
//            }
//            if (message.text == "/start") {
//                sendTextContent(chatId, "sdsdsdsdsds")
//            }


            when (message.text) {
                "/start" -> {
                    sendTextContent(chatId, "*Добро пожаловать* \n Поддержите нас доннатами! \n Можно отправить доннат по номеру карты --> 4444 8888 1035 1985")
                }
                else -> {
                    val chatGptRequest = ChatGptRequestModel(
                        Constants.CHAT_GPT_MODEL, listOf(Message("user", message.text)), 0.7
                    )
                    scope.async {
                        repository.sendMessage(chatGptRequest).onEach { result ->
                            println(TAG + result.message)
                            when (result) {
                                is Resource.Success -> {
                                    result.data?.choices?.let {
                                        if (it.isNotEmpty()) {
                                            sendTextContent(chatId, it[0].message.content)
                                        }
                                    }
                                }
                                is Resource.Error -> {
                                    sendTextContent(chatId, "Error server")
                                }

                                is Resource.Loading -> {
                                    typingState(chatId, "typing")
                                }
                            }
                        }.launchIn(this)
                    }
                }
            }

        }


    }

    private fun sendTextContent(chatId: String, responseText: String) {

        val responseMessage = SendMessage()
        responseMessage.chatId = chatId
        responseMessage.text = responseText
        responseMessage.parseMode = ParseMode.HTML

        responseMessage.enableMarkdown(true)
        execute(responseMessage)
    }


    private fun typingState(chatId: String, action: String) {
        val responseMessage = SendChatAction(chatId, action)
        execute(responseMessage)
    }

}


//    fun sendMultiFile() {
//        val send = SendMediaGroup()
////        send.medias.add(element = )
//        val media = object : InputMedia() {
//            override fun getType(): String {
//                return ""
//            }
//
//        }
//        media.caption = "dasdasdas"
//        media.parseMode = ParseMode.MARKDOWN
//        media.newMediaFile = File
//
//    }





