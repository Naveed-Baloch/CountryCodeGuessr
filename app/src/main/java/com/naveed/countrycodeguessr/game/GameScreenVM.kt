package com.naveed.countrycodeguessr.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveed.countrycodeguessr.game.components.GameScreenEvent
import com.naveed.countrycodeguessr.game.components.GameState
import com.naveed.countrydatahelper.Country
import com.naveed.countrydatahelper.CountryDataHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GameScreenVM @Inject constructor() : ViewModel() {
    var gameState by mutableStateOf(GameState())
        private set


    private val _uiEvent = Channel<GameScreenEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        getQuestion()
    }

    private fun getQuestion() {
        val countries = CountryDataHelper.getCountriesList()
        val correctCountry = getRandomCountry(countries = countries)
        val options = getOptions(correctCountry = correctCountry, countries = countries)
        gameState = gameState.copy(isSuccessful = false, currentCountry = correctCountry, options = options)
    }

    fun onEvent(event: GameScreenEvent) {
        when (event) {
            is GameScreenEvent.ChooseAnswer -> { gameState = gameState.copy(selectedAnswer = event.answer) }
            is GameScreenEvent.Submit -> checkResult()
            is GameScreenEvent.PlayNext -> getQuestion()
            else -> {}
        }
    }

    private fun checkResult() {
        if (gameState.selectedAnswer == gameState.currentCountry.dailCode) {
            gameState = gameState.copy(isSuccessful = true, selectedAnswer = "")
        } else {
            viewModelScope.launch {
                _uiEvent.send(GameScreenEvent.ShowSnakbar("Incorrect Answer, Try Again"))
            }
        }
    }

    private fun getRandomCountry(countries: List<Country>): Country {
        val randomIndex = Random.nextInt(countries.size)
        return countries[randomIndex]
    }

    private fun getOptions(correctCountry: Country, countries: List<Country>): List<Country> {
        val options = mutableListOf(correctCountry)
        while (options.size < 4) {
            val randomCountry = getRandomCountry(countries)
            if (!options.contains(randomCountry)) {
                options.add(randomCountry)
            }
        }
        return options.shuffled()
    }
}