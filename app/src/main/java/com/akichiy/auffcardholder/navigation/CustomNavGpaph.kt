package com.akichiy.auffcardholder.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.rememberGraphicsLayer
import com.akichiy.auffcardholder.presentation.screens.cards.CardsScreen
import com.akichiy.auffcardholder.presentation.screens.creation.CreateCardScreen
import com.akichiy.auffcardholder.presentation.screens.editing.EditCardScreen

@Composable
fun CustomNavGraph() {

    val screen = remember {
        mutableStateOf<CustomScreen>(CustomScreen.Cards)
    }
    val currentScreen = screen.value

    when(currentScreen) {
        CustomScreen.Cards -> {
            CardsScreen(
                onCardClick = {
                    screen.value = CustomScreen.EditCard(it.id)
                },
                onAddCardClick = {
                    screen.value = CustomScreen.CreateCard
                }
            )
        }
        CustomScreen.CreateCard -> {
            CreateCardScreen(
                onFinished = {
                    screen.value = CustomScreen.Cards
                }
            )
        }
        is CustomScreen.EditCard -> {
            EditCardScreen(
                cardId = currentScreen.cardId,
                onFinished = {
                    screen.value = CustomScreen.Cards
                }
            )
        }
    }

}

sealed interface CustomScreen {

    data object Cards : CustomScreen

    data object CreateCard : CustomScreen

    data class EditCard(val cardId: Int) : CustomScreen
}