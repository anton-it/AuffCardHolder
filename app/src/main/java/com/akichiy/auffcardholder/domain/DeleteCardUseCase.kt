package com.akichiy.auffcardholder.domain

import javax.inject.Inject

class DeleteCardUseCase @Inject constructor(
    private val repository: CardsRepository
) {

    suspend operator fun invoke(cardId: Int) {
        repository.deleteCard(cardId)
    }
}