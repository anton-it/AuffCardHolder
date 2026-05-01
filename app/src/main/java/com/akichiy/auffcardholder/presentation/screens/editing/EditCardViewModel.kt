package com.akichiy.auffcardholder.presentation.screens.editing

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akichiy.auffcardholder.data.CardsRepositoryImpl
import com.akichiy.auffcardholder.domain.AddCardUseCase
import com.akichiy.auffcardholder.domain.Card
import com.akichiy.auffcardholder.domain.ContentItem
import com.akichiy.auffcardholder.domain.DeleteCardUseCase
import com.akichiy.auffcardholder.domain.EditCardUseCase
import com.akichiy.auffcardholder.domain.GetCardUseCase
import com.akichiy.auffcardholder.presentation.screens.creation.CreateCardCommand
import com.akichiy.auffcardholder.presentation.screens.creation.CreateCardState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditCardViewModel.Factory::class)
class EditCardViewModel @AssistedInject constructor(
    private val editCardUseCase: EditCardUseCase,
    private val getCardUseCase: GetCardUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    @Assisted("cardId") private val cardId: Int
) : ViewModel() {

    private val _state = MutableStateFlow<EditCardState>(EditCardState.Initial)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                val card = getCardUseCase(cardId)
                EditCardState.Editing(card)
            }
        }
    }

    fun processCommand(command: EditCardCommand) {
        when (command) {
            EditCardCommand.Back -> {
                _state.update { EditCardState.Finished }
            }

            is EditCardCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is EditCardState.Editing) {
                        val newContent = previousState.card
                            .content
                            .mapIndexed { index, contentItem ->
                                if (index == command.index && contentItem is ContentItem.Text) {
                                    contentItem.copy(content = command.content)
                                } else {
                                    contentItem
                                }
                            }
                        val newCard = previousState.card.copy(content = newContent)
                        previousState.copy(
                            card = newCard
                        )
                    } else {
                        previousState
                    }
                }
            }

            is EditCardCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is EditCardState.Editing) {
                        val newCard = previousState.card.copy(title = command.title)
                        previousState.copy(card = newCard)
                    } else {
                        previousState
                    }
                }
            }

            EditCardCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is EditCardState.Editing) {
                            val card = previousState.card
                            val content = card.content.filter {
                                it !is ContentItem.Text || it.content.isNotBlank()
                            }
                            editCardUseCase(card.copy(content = content))
                            EditCardState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }

            EditCardCommand.Delete -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is EditCardState.Editing) {
                            val card = previousState.card
                            deleteCardUseCase(card.id)
                            EditCardState.Finished
                        } else {
                            previousState
                        }
                    }
                }

            }

            is EditCardCommand.AddImage -> {
                _state.update { previousState ->
                    if (previousState is EditCardState.Editing) {
                        val oldCard = previousState.card
                        oldCard.content.toMutableList().apply {
                            val lastItem = last()
                            if (lastItem is ContentItem.Text && lastItem.content.isBlank()) {
                                removeAt(lastIndex)
                            }
                            add(ContentItem.Image(command.uri.toString()))
                            add(ContentItem.Text(""))
                        }.let {
                            val newCard = oldCard.copy(content = it)
                            previousState.copy(card = newCard)
                        }
                    } else {
                        previousState
                    }
                }
            }
            is EditCardCommand.DeleteImage -> {
                _state.update { previousState ->
                    if (previousState is EditCardState.Editing) {
                        val oldCard = previousState.card
                        oldCard.content.toMutableList().apply {
                            removeAt(command.index)
                        }.let {
                            val newCard = oldCard.copy(content = it)
                            previousState.copy(card = newCard)
                        }
                    } else{
                        previousState
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface  Factory {
        fun create(
            @Assisted("cardId") cardId: Int
        ): EditCardViewModel
    }
}

sealed interface EditCardCommand {

    data class InputTitle(val title: String) : EditCardCommand

    data class InputContent(val content: String, val index: Int) : EditCardCommand

    data class AddImage(val uri: Uri) : EditCardCommand

    data class DeleteImage(val index: Int) : EditCardCommand

    data object Save : EditCardCommand

    data object Back : EditCardCommand

    data object Delete : EditCardCommand

}

sealed interface EditCardState {

    data object Initial : EditCardState


    data class Editing(
        val card: Card
    ) : EditCardState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                    card.title.isBlank() -> false
                    card.content.isEmpty() -> false
                    else -> {
                        card.content.any {
                            it !is ContentItem.Text || it.content.isNotBlank()
                        }
                    }
                }
            }
    }

    data object Finished : EditCardState
}