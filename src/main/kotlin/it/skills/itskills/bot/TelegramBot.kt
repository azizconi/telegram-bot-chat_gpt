package it.skills.itskills.bot


import it.skills.itskills.CHAT_GPT_MODEL
import it.skills.itskills.TELEGRAM_BOT_TOKEN
import it.skills.itskills.TELEGRAM_BOT_USERNAME
import it.skills.itskills.bot.data.remote.retrofit.RetrofitInstance
import it.skills.itskills.bot.data.repository.ChatGptRepository
import it.skills.itskills.bot.data.request.chat_gpt.ChatGptRequestModel
import it.skills.itskills.bot.data.request.chat_gpt.Message
import it.skills.itskills.bot.repository.ChatGptRepositoryImpl
import it.skills.itskills.bot.utils.Constants
import it.skills.itskills.bot.utils.Resource
import it.skills.itskills.model.entity.MessageModel
import it.skills.itskills.model.entity.UserEntity
import it.skills.itskills.model.response.UserResponseModel
import it.skills.itskills.service.UserService
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
class TelegramBot(
    private val service: UserService
) : TelegramLongPollingBot() {
    override fun getBotToken(): String {
        return TELEGRAM_BOT_TOKEN
    }

    override fun getBotUsername(): String {
        return TELEGRAM_BOT_USERNAME
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
                val id: Long = message.from.id

                when (message.text) {
                    "/start" -> {
                        sendTextContent(
                            chatId,
                            "*Добро пожаловать* \n Поддержите нас донатами! \n Можно отправить донат по номеру карты --> 4444 8888 1035 1985"
                        )
                    }
                    "/clear" -> {
                        try {
                            val user = service.getUserById(id)
                            user?.let {
                                service.deleteUser(it)
                            }
                            sendTextContent(chatId, "Напишите тему про, которую вы хотите поговорить:)")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    else -> {

                        println("Question: ${message.text} \n ${Date()}")
                        val chatGptRequest = try {
                            val user = service.getUserById(id)

                            if (user == null) service.addUser(UserEntity())


                            val convertUser = UserResponseModel.toUserResponse(user!!)

                            val messageForChat = mutableListOf<Message>()
                            convertUser.theme.let {
                                messageForChat.add(Message("system", it))
                            }

                            println()
                            convertUser.messages.forEach {
                                messageForChat.add(Message("user", it.question))
                                messageForChat.add(Message("assistant", it.ask))
                            }

                            messageForChat.add(Message("user", message.text))

                            ChatGptRequestModel(
                                CHAT_GPT_MODEL, messageForChat/*listOf(Message("user", message.text))*/, 0.7
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            println("error " + e.message)
                            ChatGptRequestModel(
                                CHAT_GPT_MODEL, listOf(Message("user", message.text)), 0.7
                            )
                        }

//                        val chatGptRequest = ChatGptRequestModel(
//                            Constants.CHAT_GPT_MODEL, listOf(Message("user", message.text)), 0.7
//                        )
                        repository.sendMessage(chatGptRequest).onEach { result ->
                            println(TAG + result.message)
                            when (result) {
                                is Resource.Success -> {


                                    result.data?.choices?.let {
                                        if (it.isNotEmpty()) {

                                            try {
                                                val userEntity = service.getUserById(id)

                                                if (userEntity != null) {

                                                    service.addMessage(
                                                        MessageModel(
                                                            question = message.text,
                                                            ask = it[0].message.content,
                                                            user = userEntity
                                                        )
                                                    )
                                                } else {
                                                    val username: String? = message.from?.userName

                                                    val user = UserEntity(
                                                        id,
                                                        username,
                                                        message.text,
                                                        emptyList()
                                                    )

                                                    service.addUser(user)
                                                }


                                            } catch (e: Exception) {
                                                val username: String? = message.from?.userName

                                                val user = UserEntity(
                                                    id,
                                                    username,
                                                    message.text,
                                                    emptyList()
                                                )

                                                service.addUser(user)
                                                e.printStackTrace()
                                            }


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