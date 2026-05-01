package com.akichiy.auffcardholder.presentation.screens.editing

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akichiy.auffcardholder.R
import com.akichiy.auffcardholder.domain.ContentItem
import com.akichiy.auffcardholder.presentation.screens.creation.CreateCardCommand
import com.akichiy.auffcardholder.presentation.ui.theme.Content
import com.akichiy.auffcardholder.presentation.ui.theme.CustomIcons
import com.akichiy.auffcardholder.utils.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCardScreen(
    modifier: Modifier = Modifier,
    cardId: Int,
    viewModel: EditCardViewModel = hiltViewModel { factory: EditCardViewModel.Factory ->
        factory.create(cardId)
    },
    onFinished: () -> Unit

) {
    val state = viewModel.state.collectAsState()
    val currentState = state.value

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.processCommand(EditCardCommand.AddImage(it))
            }
        }
    )

    when (currentState) {
        is EditCardState.Editing -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.edit_card),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        actions = {
                            Icon(
                                modifier = Modifier
                                    .clickable {
                                        imagePicker.launch("image/*")
                                    }
                                    .padding(end = 16.dp),
                                imageVector = CustomIcons.AddPhoto,
                                contentDescription = stringResource(R.string.add_photo_from_gallery),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                modifier = Modifier
                                    .padding(end = 24.dp)
                                    .clickable { viewModel.processCommand(EditCardCommand.Delete) },
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.delete_card)
                            )
                        },
                        navigationIcon = {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 8.dp)
                                    .clickable { viewModel.processCommand(EditCardCommand.Back) },
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding)

                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        value = currentState.card.title,
                        onValueChange = {
                            viewModel.processCommand(EditCardCommand.InputTitle(it))
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface

                        ),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.title),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                        }
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = DateFormatter.formatDateToString(currentState.card.updatedAt),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    //////////////////////////
                    /////TextContent
                    Content(
                        modifier = Modifier.weight(1f),
                        content = currentState.card.content,
                        onDeleteImageClick = {
                            viewModel.processCommand(
                                EditCardCommand.DeleteImage(it)
                            )
                        },
                        onTextChanged = {index, text ->
                            viewModel.processCommand(
                                EditCardCommand.InputContent(
                                    content = text,
                                    index = index
                                )
                            )
                        }
                    )

                    Button(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(),
                        onClick = {
                            viewModel.processCommand(EditCardCommand.Save)
                        },
                        shape = RoundedCornerShape(10.dp),
                        enabled = currentState.isSaveEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.1f
                            ),
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.save_card)
                        )
                    }
                }
            }
        }

        EditCardState.Finished -> {
            LaunchedEffect(key1 = Unit) {
                onFinished()
            }
        }

        EditCardState.Initial -> {}
    }
}

@Composable
private fun TextContent(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        value = text,
        onValueChange = onTextChange,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface

        ),
        placeholder = {
            Text(
                text = stringResource(R.string.note_something_down),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        }
    )
}