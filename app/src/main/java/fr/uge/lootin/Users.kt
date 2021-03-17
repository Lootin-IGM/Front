package fr.uge.lootin

class Users(val id: String, val description: String, val firstName: String, val lastName: String, val login: String, val gender: String, val image: String, val age: Int) {

    override fun toString(): String {
        return "Users(id='$id', description='$description', firstName='$firstName', lastName='$lastName', login='$login', gender='$gender', image='$image', age=$age)"
    }
}