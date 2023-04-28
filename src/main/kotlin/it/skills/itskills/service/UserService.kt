package it.skills.itskills.service


import it.skills.itskills.model.entity.MessageModel
import it.skills.itskills.model.entity.UserEntity
import it.skills.itskills.repository.MessageRepository
import it.skills.itskills.repository.UserRepository
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
@RequiredArgsConstructor
class UserService(
    private val repository: UserRepository,
    private val messageRepository: MessageRepository
) {


    fun getUserById(id: Long): UserEntity? = repository.getReferenceById(id)
    fun getUserByUsername(username: String) = repository.findByUsername(username)

    fun addUser(user: UserEntity) = repository.save(user)
    fun deleteUser(user: UserEntity) = repository.delete(user)

    fun addMessage(message: MessageModel): MessageModel = messageRepository.save(message)

}