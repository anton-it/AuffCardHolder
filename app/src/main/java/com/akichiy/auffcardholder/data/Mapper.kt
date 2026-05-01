package com.akichiy.auffcardholder.data

import com.akichiy.auffcardholder.domain.Card
import com.akichiy.auffcardholder.domain.ContentItem
import kotlinx.serialization.json.Json

fun Card.toDbModel(): CardDbModel {
    return CardDbModel(id, title, updatedAt, isPinned)
}

fun List<ContentItem>.toContentItemDbModels(cardId: Int): List<ContentItemDbModel> {
    return mapIndexed {index, contentItem ->
        when(contentItem) {
            is ContentItem.Image -> {
                ContentItemDbModel(
                    cardId = cardId,
                    contentType = ContentType.IMAGE,
                    content = contentItem.url,
                    order = index
                )
            }
            is ContentItem.Text -> {
                ContentItemDbModel(
                    cardId = cardId,
                    contentType = ContentType.TEXT,
                    content = contentItem.content,
                    order = index
                )
            }
        }
    }
}

fun List<ContentItemDbModel>.toContentItems(): List<ContentItem> {
    return map {contentItem ->
       when(contentItem.contentType) {
           ContentType.TEXT -> {
               ContentItem.Text(content = contentItem.content)
           }
           ContentType.IMAGE -> {
               ContentItem.Image(url = contentItem.content)
           }
       }
    }
}

fun CardWithContentDbModel.toEntity(): Card {
    return Card(
        id = cardDbModel.id,
        title = cardDbModel.title,
        content = content.toContentItems(),
        updatedAt = cardDbModel.updatedAt,
        isPinned = cardDbModel.isPinned
    )
}

fun List<CardWithContentDbModel>.toEntities(): List<Card> {
    return this.map { it.toEntity() }
}