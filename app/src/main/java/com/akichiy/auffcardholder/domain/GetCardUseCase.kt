package com.akichiy.auffcardholder.domain

import javax.inject.Inject

class GetCardUseCase @Inject constructor(
    private val repository: CardsRepository
) {

    suspend operator fun invoke(cardId: Int): Card {
        return repository.getCard(cardId)
    }
}