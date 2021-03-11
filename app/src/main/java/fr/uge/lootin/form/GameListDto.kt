package fr.uge.lootin.form

class GameListDto(val games: List<GameDto>) {
    override fun toString(): String {
        return "GameListDto(games=$games)"
    }
}