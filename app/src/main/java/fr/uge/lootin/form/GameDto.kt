package fr.uge.lootin.form

class GameDto(val id: Long, val gameName: String, val image: ImageDto) {
    override fun toString(): String {
        return "GameDto=(id='$id', gameName='$gameName', image='$image')"
    }
}