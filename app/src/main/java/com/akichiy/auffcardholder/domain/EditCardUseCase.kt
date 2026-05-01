package com.akichiy.auffcardholder.domain

import javax.inject.Inject

class EditCardUseCase @Inject constructor(
    private val repository: CardsRepository
) {

    suspend operator fun invoke(card: Card) {
        repository.editCard(
            card.copy(
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}