package it.skills.itskills.repository

import it.skills.itskills.model.entity.MessageModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository: JpaRepository<MessageModel, Long>