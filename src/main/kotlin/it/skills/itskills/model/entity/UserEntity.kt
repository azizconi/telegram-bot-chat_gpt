package it.skills.itskills.model.entity

import javax.persistence.*

@Entity
@Table(name = "user")
data class UserEntity (
    @Id
    val id: Long,
    @Column(name = "username")
    val username: String?,
    @Column(name = "theme", length = 240000)
    val theme: String,

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user", fetch = FetchType.EAGER)
    val messages: List<MessageModel>
) {
    constructor(): this(1, "", "", listOf())
}