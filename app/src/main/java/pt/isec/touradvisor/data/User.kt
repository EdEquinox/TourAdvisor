package pt.isec.touradvisor.data

import com.google.firebase.auth.FirebaseUser

data class User(
    val name : String? = null,
    val email: String,
    val picture : String? = null
) {
    override fun toString(): String {
        return name ?: ""
    }
}

fun FirebaseUser.toUser() : User {
    val displayName = this.displayName ?: ""
    val email = this.email ?: ""
    val picture = this.photoUrl.toString() ?:""

    return User(displayName, email, picture)
}