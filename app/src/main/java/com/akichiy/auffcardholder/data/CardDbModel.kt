package com.akichiy.auffcardholder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val updatedAt: Long,
    val isPinned: Boolean
)
