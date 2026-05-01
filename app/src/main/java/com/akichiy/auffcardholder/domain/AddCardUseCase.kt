package com.akichiy.auffcardholder.domain

import javax.inject.Inject

class AddCardUseCase @Inject constructor(
    private val repository: CardsRepository
) {

    suspend operator fun invoke(
        title: String,
        content: List<ContentItem>
    ) {
        repository.addCard(
            title = title,
            content = content,
            isPinned = false,
            updateAt = System.currentTimeMillis()
        )
    }
}