package it.skills.itskills.bot


import it.skills.itskills.bot.data.remote.retrofit.RetrofitInstance
import it.skills.itskills.bot.data.repository.ChatGptRepository
import it.skills.itskills.bot.data.request.chat_gpt.ChatGptRequestModel
import it.skills.itskills.bot.data.request.chat_gpt.Message
import it.skills.itskills.bot.repository.ChatGptRepositoryImpl
import it.skills.itskills.bot.utils.Constants
import it.skills.itskills.bot.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.File
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.Voice
import java.util.*
import kotlin.coroutines.coroutineContext

@Service
class TelegramBot : TelegramLongPollingBot() {
    override fun getBotToken(): String {
        return Constants.TELEGRAM_BOT_TOKEN
    }

    override fun getBotUsername(): String {
        return Constants.TELEGRAM_BOT_USERNAME
    }

    val repository: ChatGptRepository = ChatGptRepositoryImpl(RetrofitInstance.api())

    companion object {
        const val TAG = "TelegramBot: "
    }

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()) {


            val message = update.message
            val chatId = message.chatId.toString()



            try {
                println("username: ${message.from.userName}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

            if (message.hasVoice()) {
                val voice: Voice = update.message.voice
                val fileId: String = voice.fileId

                val getFile = GetFile()
                getFile.fileId = fileId

                val file: File = execute(getFile)

                val fileUrl: String = "https://api.telegram.org/file/bot" + botToken + "/" + file.filePath

//                oga
                val dir = directory("voice/")
                val inputFile = java.io.File(dir.absolutePath, "${file.fileId}.oga")


                val downloadRequest = Request.Builder()
                    .url(fileUrl)
                    .build()


                CoroutineScope(Dispatchers.IO).launch {

                    OkHttpClient().newCall(downloadRequest).execute().body?.let {
                        inputFile.outputStream().use { output ->
                            it.byteStream().use { input ->

                                input.copyTo(output)
                            }
                        }
                    }

                    println(TAG + " ${file.filePath}")
                }
            }

            if (message.hasText()) {
                when (message.text) {
                    "/start" -> {
                        sendTextContent(
                            chatId,
                            "*Добро пожаловать* \n Поддержите нас донатами! \n Можно отправить донат по номеру карты --> 4444 8888 1035 1985"
                        )
                    }
                    else -> {
                        println("Question: ${message.text} \n ${Date()}")
                        val chatGptRequest = ChatGptRequestModel(
                            Constants.CHAT_GPT_MODEL, listOf(Message("user", message.text)), 0.7
                        )
                        repository.sendMessage(chatGptRequest).onEach { result ->
                            println(TAG + result.message)
                            when (result) {
                                is Resource.Success -> {
                                    result.data?.choices?.let {
                                        if (it.isNotEmpty()) {
                                            sendTextContent(chatId, it[0].message.content)
                                        }
                                    }
                                    coroutineContext.cancel()
                                }
                                is Resource.Error -> {
                                    sendTextContent(chatId, "Error server")
                                    coroutineContext.cancel()
                                }

                                is Resource.Loading -> {
                                    typingState(chatId, "typing")
                                }
                            }
                        }.launchIn(CoroutineScope(Dispatchers.IO))
                    }
                }

            }

        }


    }

    private fun directory(type: String): java.io.File {

        val dir = java.io.File("../files/$type")
        if (!dir.exists()) dir.mkdirs()

        return dir
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