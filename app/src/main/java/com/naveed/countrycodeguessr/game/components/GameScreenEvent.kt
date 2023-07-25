package com.naveed.countrycodeguessr.game.components

sealed class GameScreenEvent{
    data class ChooseAnswer(val answer: String): GameScreenEvent()
    data class ShowSnakbar(val message: String): GameScreenEvent()
    object PlayNext: GameScreenEvent()
    object Submit: GameScreenEvent()
}
