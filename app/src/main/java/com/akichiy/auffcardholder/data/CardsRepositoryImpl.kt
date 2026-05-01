package com.akichiy.auffcardholder.data

import com.akichiy.auffcardholder.domain.Card
import com.akichiy.auffcardholder.domain.CardsRepository
import com.akichiy.auffcardholder.domain.ContentItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CardsRepositoryImpl @Inject constructor(
    private val cardsDao: CardsDao,
    private val imageFileManager: ImageFileManager
): CardsRepository {


    override suspend fun addCard(
        title: String,
        content: List<ContentItem>,
        isPinned: Boolean,
        updateAt: Long
    ) {
        val processedContent = content.processedForStorage()
        val cardDbModel = CardDbModel(0, title,  updateAt, isPinned)
        cardsDao.addCardWithContent(cardDbModel, processedContent)
    }

    override suspend fun deleteCard(cardId: Int) {
        val card = cardsDao.getCard(cardId).toEntity()
        cardsDao.deleteCard(cardId)

        card.content.filterIsInstance<ContentItem.Image>()
            .map { it.url }
            .forEach {
                imageFileManager.deleteImage(it)
            }
    }

    override suspend fun editCard(card: Card) {
        val oldCard = cardsDao.getCard(card.id).toEntity()

        val oldUrls = oldCard.content.filterIsInstance<ContentItem.Image>().map { it.url }
        val newUrls = card.content.filterIsInstance<ContentItem.Image>().map { it.url }
        val removedUrl = oldUrls - newUrls

        removedUrl.forEach {
            imageFileManager.deleteImage(it)
        }

        val processedContent = card.content.processedForStorage()
        val processedCard = card.copy(content = processedContent)

        cardsDao.updateCard(
            cardDbModel = processedCard.toDbModel(),
            content = processedContent.toContentItemDbModels(card.id)
        )
    }

    override fun getAllCards(): Flow<List<Card>> {
        return cardsDao.getAllCards().map { it.toEntities() }
    }

    override suspend fun getCard(cardId: Int): Card {
        return cardsDao.getCard(cardId).toEntity()
    }

    override fun searchCards(query: String): Flow<List<Card>> {
        return cardsDao.searchCards(query).map { it.toEntities() }
    }

    override suspend fun switchPinStatus(cardId: Int) {
        cardsDao.switchPinnedStatus(cardId)
    }

    private suspend fun List<ContentItem>.processedForStorage(): List<ContentItem> {
        return map { contentItem ->
            when(contentItem) {
                is ContentItem.Image -> {
                    if (imageFileManager.isInternal(contentItem.url)) {
                        contentItem
                    } else {
                        val internalPatch = imageFileManager.copyImageToInternalStorage(contentItem.url)
                        ContentItem.Image(internalPatch)
                    }
                }
                is ContentItem.Text -> contentItem
            }
        }
    }
}