package fr.uge.lootin.models

class GameListDto(val games: List<GameDto>) {
    override fun toString(): String {
        return "GameListDto(games=$games)"
    }
}