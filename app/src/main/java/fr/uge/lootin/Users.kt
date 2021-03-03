package fr.uge.lootin

class Users(val id: String, val firstName: String, val lastName: String, val login: String) {

    override fun toString(): String {
        return "User(id='$id', firstName='$firstName', lastName='$lastName', login='$login')"
    }
}