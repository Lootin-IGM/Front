package fr.uge.lootin

class UserFull(val id: String, val firstName: String, val lastName: String, val games: Set<Game>, val login: String) {
    class Game(val id: String, val gameName: String, val imageURL: String)
}