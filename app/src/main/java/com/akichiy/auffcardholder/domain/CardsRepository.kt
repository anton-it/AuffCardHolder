package com.akichiy.auffcardholder.domain

import kotlinx.coroutines.flow.Flow

interface CardsRepository {

    suspend fun addCard(
        title: String,
        content: List<ContentItem>,
        isPinned: Boolean,
        updateAt: Long
    )

    suspend fun deleteCard(cardId: Int)

    suspend fun editCard(card: Card)

    fun getAllCards(): Flow<List<Card>>

    suspend fun getCard(cardId: Int): Card

    fun searchCards(query: String): Flow<List<Card>>

    suspend fun switchPinStatus(cardId: Int)
}