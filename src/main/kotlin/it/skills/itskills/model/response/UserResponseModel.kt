package it.skills.itskills.model.response

import it.skills.itskills.model.entity.MessageModel
import it.skills.itskills.model.entity.UserEntity

data class UserResponseModel(
    val username: String?,
    val theme: String,
    val messages: List<MessagesResponseModel>
) {
    companion object {
        fun toUserResponse(user: UserEntity): UserResponseModel {
            return UserResponseModel(
                user.username,
                user.theme,
                MessagesResponseModel.toMessagesResponseModel(user.messages)
            )
        }
    }
}


data class MessagesResponseModel(
    val question: String,
    val ask: String
) {
    companion object {
        fun toMessagesResponseModel(messages: List<MessageModel>): List<MessagesResponseModel> {
            return messages.map {
                MessagesResponseModel(
                    it.question,
                    it.ask
                )
            }
        }
    }
}