@file:Suppress("OPT_IN_USAGE")

package com.akichiy.auffcardholder.presentation.screens.cards

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akichiy.auffcardholder.data.CardsRepositoryImpl
import com.akichiy.auffcardholder.domain.AddCardUseCase
import com.akichiy.auffcardholder.domain.Card
import com.akichiy.auffcardholder.domain.DeleteCardUseCase
import com.akichiy.auffcardholder.domain.EditCardUseCase
import com.akichiy.auffcardholder.domain.GetAllCardsUseCase
import com.akichiy.auffcardholder.domain.GetCardUseCase
import com.akichiy.auffcardholder.domain.SearchCardsUseCase
import com.akichiy.auffcardholder.domain.SwitchPinnedStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    private val getAllCardsUseCase: GetAllCardsUseCase,
    private val searchCardsUseCase: SearchCardsUseCase,
    private val switchPinnedStatusUseCase: SwitchPinnedStatusUseCase
) : ViewModel() {

    private val query = MutableStateFlow("")

    private val _state = MutableStateFlow(CardsScreenState())
    val state = _state.asStateFlow()

    init {
        query
            .onEach { inputQuery ->
                _state.update { it.copy(query = inputQuery) }
            }
            .flatMapLatest { inputQuery ->
                if (inputQuery.isBlank()) {
                    getAllCardsUseCase()
                } else {
                    searchCardsUseCase(inputQuery)
                }
            }
            .onEach { cards ->
                val pinnedCards = cards.filter { it.isPinned }
                val otherCards = cards.filter { !it.isPinned }
                _state.update {
                    it.copy(pinnedCards = pinnedCards, otherCards = otherCards)
                }
            }
            .launchIn(viewModelScope)
    }

//    private fun addSomeCards() {
//        viewModelScope.launch {
//            repeat(50) {
//                addCardUseCase(
//                    title = "Title $it Title $it Title $it Title $it Title $it Title $it Title $it Title $it Title $it Title $it Title $it Title $it Title $it Title $it ",
//                    content = "Content $it Content $it Content $it Content $it Content $it Content $it Content $it Content $it Content $it Content $it Content $it Content $it Content $it "
//                )
//            }
//        }
//    }

    fun processCommand(command: CardsCommand) {
        viewModelScope.launch {
            when (command) {
                is CardsCommand.InputSearchQuery -> {
                    query.update { command.query.trim() }
                }

                CardsCommand.LoadingScreen -> {}
                is CardsCommand.SwitchPinnedStatus -> {
                    switchPinnedStatusUseCase(command.cardId)
                }
            }
        }
    }
}

sealed interface CardsCommand {

    data object LoadingScreen : CardsCommand

    data class InputSearchQuery(val query: String) : CardsCommand

    data class SwitchPinnedStatus(val cardId: Int) : CardsCommand

}

data class CardsScreenState(
    val query: String = "",
    val pinnedCards: List<Card> = listOf(),
    val otherCards: List<Card> = listOf()
)