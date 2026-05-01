package com.akichiy.auffcardholder.domain

data class Card(
    val id: Int,
    val title: String,
    val content: List<ContentItem>,
    val updatedAt: Long,
    val isPinned: Boolean
)
