package fr.uge.lootin.models

class GameDto(val id: Long, val gameName: String, val image: ImageDto) {
    override fun toString(): String {
        return "GameDto=(id='$id', gameName='$gameName', image='$image')"
    }
}