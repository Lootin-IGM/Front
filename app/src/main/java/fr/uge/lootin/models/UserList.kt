package fr.uge.lootin.models

class UserList(val users : List<Users>) {
    override fun toString(): String {
        return "UserList(users=$users)"
    }
}