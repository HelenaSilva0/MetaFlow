package com.example.metaflow.db.fb

import com.example.metaflow.model.User

class FBUser {
    var name: String? = null
    var email: String? = null
    var xp: Int = 0
    var streak: Int = 0
    var lastActivityDate: Long = 0L
    var badges: List<String> = emptyList()
    var totalCompleted: Int = 0
    var profilePic: String? = null
    var theme: String? = "Sistema"

    fun toUser() = User(
        name = name ?: "",
        email = email ?: "",
        xp = xp,
        streak = streak,
        lastActivityDate = lastActivityDate,
        badges = badges,
        totalCompleted = totalCompleted,
        profilePic = profilePic,
        theme = theme ?: "Sistema"
    )
}

fun User.toFBUser(): FBUser {
    val fbUser = FBUser()
    fbUser.name = this.name
    fbUser.email = this.email
    fbUser.xp = this.xp
    fbUser.streak = this.streak
    fbUser.lastActivityDate = this.lastActivityDate
    fbUser.badges = this.badges
    fbUser.totalCompleted = this.totalCompleted
    fbUser.profilePic = this.profilePic
    fbUser.theme = this.theme
    return fbUser
}
