package com.akichiy.auffcardholder.data


import androidx.room.Entity
import androidx.room.ForeignKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "content",
    primaryKeys = ["cardId", "order"],
    foreignKeys = [
        ForeignKey(
            entity = CardDbModel::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ContentItemDbModel(
    val cardId: Int,
    val contentType: ContentType,
    val content: String,
    val order: Int

    )

enum class ContentType {

    TEXT, IMAGE
}