package com.naveed.countrycodeguessr.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naveed.countrycodeguessr.game.components.GameScreenEvent
import com.naveed.countrycodeguessr.game.components.GameState
import com.naveed.countrycodeguessr.model.Country
import com.naveed.countrycodeguessr.ui.theme.Green


@JvmOverloads
@Composable
fun GameScreen(viewModel: GameScreenVM = hiltViewModel(), scaffoldState: ScaffoldState) {
    val state = viewModel.gameState

    LaunchedEffect(key1 = viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is GameScreenEvent.ShowSnakbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(message = event.message)
                }
                else -> Unit
            }
        }
    }

    Questions(
        state = state,
        onAnswerSelected = { ans -> viewModel.onEvent(GameScreenEvent.ChooseAnswer(ans)) },
        onSubmit = { viewModel.onEvent(GameScreenEvent.Submit) }
    )

    QuizSuccess(state = state) { viewModel.onEvent(GameScreenEvent.PlayNext) }
}

@Composable
fun QuizSuccess(state: GameState, onNextClick: () -> Unit) {
    if (!state.isSuccessful) return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Correct Answer\n Congratulations 🎉🎊 ",
            style = MaterialTheme.typography.headlineSmall.copy(color = Color.Black, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center),
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(50.dp))
        Button(
            onClick = onNextClick,
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Next Question")
        }
    }
}

@Composable
fun Questions(state: GameState, onAnswerSelected: (String) -> Unit, onSubmit: () -> Unit) {
    if (state.isSuccessful) return
    val currentCountry = state.currentCountry
    val selectedAnswer = state.selectedAnswer
    val options = state.options
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .requiredHeight(80.dp)
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(20.dp))
                .fillMaxSize()
                .padding(5.dp)
        ) {
            Text(
                text = "What is the Country Dial Code of ${currentCountry.name}?",
                style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
        Spacer(modifier = Modifier.height(50.dp))

        for (option in options) {
            AnswerOption(
                option = option,
                selectedAnswer = selectedAnswer,
                onAnswerSelected = { onAnswerSelected(it) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))
        Button(

            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(50.dp),
            onClick = { onSubmit() },
            enabled = state.selectedAnswer.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(Green)
        ) {
            Text(
                text = "Check Answer",
                style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            )
        }
    }
}

@Composable
fun AnswerOption(
    option: Country,
    selectedAnswer: String,
    onAnswerSelected: (String) -> Unit
) {
    val isSelected = option.dailCode == selectedAnswer
    val btnColor = if (isSelected) Green else MaterialTheme.colorScheme.primary
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAnswerSelected(option.dailCode) }
            .requiredHeight(50.dp)
            .background(btnColor, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = option.dailCode,
            style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        )
    }
}

