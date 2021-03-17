package fr.uge.lootin.models

class UserFull(val id: String, val description: String, val firstName: String, val lastName: String, val login: String, val gender: String, val image: String, val age: Int, val games: Set<Game>) {



    class Game(val id: String, val gameName: String, val imageURL: String){
        override fun toString(): String {
            return "Game(id='$id', gameName='$gameName', imageURL='$imageURL')"
        }
    }

    override fun toString(): String {
        return "UserFull(id='$id', description='$description', firstName='$firstName', lastName='$lastName', login='$login', gender='$gender', image='$image', age=$age, games=$games)"
    }


}