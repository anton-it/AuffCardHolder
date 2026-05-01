package com.akichiy.auffcardholder.presentation.screens.cards

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.akichiy.auffcardholder.R
import com.akichiy.auffcardholder.domain.Card
import com.akichiy.auffcardholder.domain.ContentItem
import com.akichiy.auffcardholder.presentation.ui.theme.OtherCardsColors
import com.akichiy.auffcardholder.presentation.ui.theme.PinnedCardsColors
import com.akichiy.auffcardholder.utils.DateFormatter

@Composable
fun CardsScreen(
    modifier: Modifier = Modifier,
    viewModel: CardsViewModel = hiltViewModel(),
    onCardClick: (Card) -> Unit,
    onAddCardClick: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCardClick,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_note),
                    contentDescription = "Button add card"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding
        ) {
            item {
                Title(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.all_cards)
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                SearchBar(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    query = state.query,
                    onQueryChange = {
                        viewModel.processCommand(CardsCommand.InputSearchQuery(it))
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.pinned)
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    itemsIndexed(
                        items = state.pinnedCards,
                        key = { _, card -> card.id }
                    ) { index, card ->
                        CardsCard(
                            modifier = Modifier.widthIn(max = 160.dp),
                            card = card,
                            onCardClick = onCardClick,
                            onLongClick = {
                                viewModel.processCommand(CardsCommand.SwitchPinnedStatus(it.id))
                            },
                            backgroundColor = PinnedCardsColors[index % PinnedCardsColors.size]
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.others)
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            itemsIndexed(
                items = state.otherCards,
                key = { _, card -> card.id }
            ) { index, card ->
                val imageUrl = card.content
                    .filterIsInstance<ContentItem.Image>()
                    .map { it.url }
                    .firstOrNull()

                if (imageUrl == null) {
                    CardsCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        card = card,
                        onCardClick = onCardClick,
                        onLongClick = {
                            viewModel.processCommand(CardsCommand.SwitchPinnedStatus(it.id))
                        },
                        backgroundColor = OtherCardsColors[index % OtherCardsColors.size]
                    )
                } else {
                    CardsCardWithImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        card = card,
                        imageUrl = imageUrl,
                        onCardClick = onCardClick,
                        onLongClick = {
                            viewModel.processCommand(CardsCommand.SwitchPinnedStatus(it.id))
                        },
                        backgroundColor = OtherCardsColors[index % OtherCardsColors.size]
                    )

                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@Composable
private fun Title(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onSurface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent

        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_cards),
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
private fun Subtitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun CardsCardWithImage(
    modifier: Modifier = Modifier,
    card: Card,
    imageUrl: String,
    backgroundColor: Color,
    onCardClick: (Card) -> Unit,
    onLongClick: (Card) -> Unit

) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .combinedClickable(
                onClick = {
                    onCardClick(card)
                },
                onLongClick = {
                    onLongClick(card)
                }
            )
    ) {

        Box {
            AsyncImage(
                modifier = Modifier
                    .heightIn(max = 130.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                model = imageUrl,
                contentDescription = stringResource(R.string.image_from_card),
                contentScale = ContentScale.FillWidth
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(16.dp))
//                    .background(
//                        brush = Brush.verticalGradient(
//                            listOf(
//                                Color.Transparent,
//                                MaterialTheme.colorScheme.onSurface
//                            )
//                        )
//                    )
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = card.title,
                    fontSize = 14.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onPrimary,
                    overflow = TextOverflow.Ellipsis
                )
//                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = DateFormatter.formatDateToString(card.updatedAt),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        card.content
            .filterIsInstance<ContentItem.Text>()
            .filter { it.content.isNotBlank() }
            .joinToString("\n") { it.content }
            .takeIf { it.isNotBlank() }
            ?.let {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = it,
                    fontSize = 16.sp,
                    maxLines = 3,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis
                )
            }

    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardsCard(
    modifier: Modifier = Modifier,
    card: Card,
    backgroundColor: Color,
    onCardClick: (Card) -> Unit,
    onLongClick: (Card) -> Unit

) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .combinedClickable(
                onClick = {
                    onCardClick(card)
                },
                onLongClick = {
                    onLongClick(card)
                }
            )
            .padding(16.dp),
    ) {
        Text(
            text = card.title,
            fontSize = 14.sp,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = DateFormatter.formatDateToString(card.updatedAt),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        card.content
            .filterIsInstance<ContentItem.Text>()
            .filter { it.content.isNotBlank() }
            .joinToString("\n") { it.content }
            .takeIf { it.isNotBlank() }
            ?.let {
                Spacer(modifier = Modifier.padding(16.dp))
                Text(
                    text = it,
                    fontSize = 16.sp,
                    maxLines = 3,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis
                )
            }

    }
}