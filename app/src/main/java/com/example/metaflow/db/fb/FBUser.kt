package com.example.metaflow.db.fb

import com.example.metaflow.model.User

class FBUser {
    var name: String? = null
    var email: String? = null
    var xp: Int = 0

    fun toUser() = User(name ?: "", email ?: "", xp)
}

fun User.toFBUser(): FBUser {
    val fbUser = FBUser()
    fbUser.name = this.name
    fbUser.email = this.email
    fbUser.xp = this.xp
    return fbUser
}
