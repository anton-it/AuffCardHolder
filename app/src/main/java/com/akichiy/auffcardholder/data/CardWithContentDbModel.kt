package com.akichiy.auffcardholder.data

import androidx.room.Embedded
import androidx.room.Relation

data class CardWithContentDbModel(
    @Embedded
    val cardDbModel: CardDbModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val content: List<ContentItemDbModel>
)
