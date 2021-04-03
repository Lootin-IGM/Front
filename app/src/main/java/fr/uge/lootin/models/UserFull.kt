package fr.uge.lootin.models

import fr.uge.lootin.form.Game
import fr.uge.lootin.models.GameDto

class UserFull(val id: String, val description: String, val firstName: String, val lastName: String, val login: String, val gender: String, val image: String, val age: Int, val games: List<GameDto>) {



    override fun toString(): String {
        return "UserFull(id='$id', description='$description', firstName='$firstName', lastName='$lastName', login='$login', gender='$gender', image='$image', age=$age, games=$games)"
    }


}