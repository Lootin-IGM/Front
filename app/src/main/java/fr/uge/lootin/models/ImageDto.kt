package fr.uge.lootin.models

class ImageDto(val id: Long, val name: String, val image: String) {
    override fun toString(): String {
        return "ImageDto=(id='$id', name='$name', image='$image')"
    }
}