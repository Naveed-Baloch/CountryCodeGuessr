package com.naveed.countrycodeguessr.game.components

import com.naveed.countrydatahelper.Country

data class GameState(
    val selectedAnswer: String = "",
    val currentCountry: Country = Country(),
    val isSuccessful: Boolean = false,
    val options: List<Country> = emptyList()
)
