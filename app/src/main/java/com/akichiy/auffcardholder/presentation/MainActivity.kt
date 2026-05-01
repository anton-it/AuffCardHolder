package com.akichiy.auffcardholder.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.akichiy.auffcardholder.navigation.NavGraph
import com.akichiy.auffcardholder.presentation.screens.cards.CardsScreen
import com.akichiy.auffcardholder.presentation.screens.creation.CreateCardScreen
import com.akichiy.auffcardholder.presentation.screens.creation.CreateCardState
import com.akichiy.auffcardholder.presentation.screens.editing.EditCardScreen
import com.akichiy.auffcardholder.presentation.ui.theme.AuffCardHolderTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuffCardHolderTheme {
                NavGraph()
            }
        }
    }
}
