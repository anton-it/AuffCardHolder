package com.akichiy.auffcardholder.presentation.screens.creation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akichiy.auffcardholder.domain.AddCardUseCase
import com.akichiy.auffcardholder.domain.ContentItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCardViewModel @Inject constructor(
    private val addCardUseCase: AddCardUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CreateCardState>(CreateCardState.Creation())
    val state = _state.asStateFlow()

    fun processCommand(command: CreateCardCommand) {
        when (command) {
            CreateCardCommand.Back -> {
                _state.update { CreateCardState.Finished }
            }

            is CreateCardCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is CreateCardState.Creation) {
                        val newContent = previousState.content
                            .mapIndexed { index, contentItem ->
                                if (index == command.index && contentItem is ContentItem.Text) {
                                    contentItem.copy(content = command.content)
                                } else {
                                    contentItem
                                }
                            }
                        previousState.copy(
                            content = newContent
                        )
                    } else {
                        previousState
                    }
                }
            }

            is CreateCardCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is CreateCardState.Creation) {
                        previousState.copy(
                            title = command.title
                        )
                    } else {
                        previousState
                    }
                }
            }

            CreateCardCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is CreateCardState.Creation) {
                            val title = previousState.title
                            val content = previousState.content.filter {
                                it !is ContentItem.Text || it.content.isNotBlank()
                            }
                            addCardUseCase(title, content)
                            CreateCardState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }

            is CreateCardCommand.AddImage -> {
                _state.update { previousState ->
                    if (previousState is CreateCardState.Creation) {
                        previousState.content.toMutableList().apply {
                            val lastItem = last()
                            if (lastItem is ContentItem.Text && lastItem.content.isBlank()) {
                                removeAt(lastIndex)
                            }
                            add(ContentItem.Image(command.uri.toString()))
                            add(ContentItem.Text(""))
                        }.let {
                            previousState.copy(content = it)
                        }
                    } else{
                        previousState
                    }
                }

//                _state.update { previousState ->
//                    if (previousState is CreateCardState.Creation) {
//                        val newItems = previousState.content.toMutableList()
//                        val lastItem = newItems.last()
//                        if (lastItem is ContentItem.Text && lastItem.content.isBlank()) {
//                            newItems.removeAt(newItems.lastIndex)
//                        }
//                        newItems.add(ContentItem.Image(command.uri.toString()))
//                        newItems.add(ContentItem.Text(""))
//                        previousState.copy(content = newItems   )
//
//                    } else{
//                        previousState
//                    }
//                }
            }

            is CreateCardCommand.DeleteImage -> {
                _state.update { previousState ->
                    if (previousState is CreateCardState.Creation) {
                        previousState.content.toMutableList().apply {
                            removeAt(command.index)
                        }.let {
                            previousState.copy(content = it)
                        }
                    } else{
                        previousState
                    }
                }
            }
        }
    }
}

sealed interface CreateCardCommand {

    data class InputTitle(val title: String) : CreateCardCommand

    data class InputContent(val content: String, val index: Int) : CreateCardCommand

    data class AddImage(val uri: Uri) : CreateCardCommand

    data class DeleteImage(val index: Int) : CreateCardCommand

    data object Save : CreateCardCommand

    data object Back : CreateCardCommand

}

sealed interface CreateCardState {


    data class Creation(
        val title: String = "",
        val content: List<ContentItem> = listOf(ContentItem.Text("")),
    ) : CreateCardState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                   title.isBlank() -> false
                    content.isEmpty() -> false
                    else -> {
                        content.any {
                            it !is ContentItem.Text || it.content.isNotBlank()
                        }
                    }
                }
            }
    }

    data object Finished : CreateCardState
}