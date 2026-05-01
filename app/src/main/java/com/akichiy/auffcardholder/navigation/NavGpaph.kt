package com.akichiy.auffcardholder.navigation

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.akichiy.auffcardholder.presentation.screens.cards.CardsScreen
import com.akichiy.auffcardholder.presentation.screens.creation.CreateCardScreen
import com.akichiy.auffcardholder.presentation.screens.editing.EditCardScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Cards.route
    ) {
        composable(Screen.Cards.route) {
            CardsScreen(
                onCardClick = {
                    //старый способ без метода в классе EditScreen
//                    navController.navigate(Screen.EditCard.route + "${it.id}")
                    navController.navigate(Screen.EditCard.createCard(it.id))
                },
                onAddCardClick = {
                    navController.navigate(Screen.CreateCard.route)
                }
            )
        }
        composable(Screen.CreateCard.route) {
            CreateCardScreen(
                onFinished = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.EditCard.route) {
            //старый способ без метода в классе EditScreen
//            val cardId = it.arguments?.getString("card_id")?.toInt() ?: 0
            val cardId = Screen.EditCard.getCardId(it.arguments)

            EditCardScreen(
                cardId = cardId,
                onFinished = {
                    navController.popBackStack()
                }
            )
        }
    }

}

sealed class Screen(val route: String) {

    data object Cards : Screen("cards")

    data object CreateCard : Screen("create_card")

    data object EditCard : Screen("edit_card/{card_id}") {

        fun createCard(cardId: Int): String {
            return "edit_card/$cardId"
        }

        fun getCardId(arguments: Bundle?): Int {
            return arguments?.getString("card_id")?.toInt() ?: 0
        }
    }
}