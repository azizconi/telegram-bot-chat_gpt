package it.skills.itskills.model.entity

import javax.persistence.*


@Entity
@Table(name = "message")
data class MessageModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "question", length = 240000)
    val question: String,
    @Column(name = "ask", length = 240000)
    val ask: String,
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: UserEntity
) {
    constructor(question: String, ask: String, user: UserEntity): this(1, question, ask, user)
    constructor(): this(1, "", "", UserEntity(1, "", "", listOf()))
}
