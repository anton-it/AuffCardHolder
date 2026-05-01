package com.akichiy.auffcardholder.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCardsUseCase @Inject constructor(
    private val repository: CardsRepository
) {

    operator fun invoke(): Flow<List<Card>> {
        return repository.getAllCards()
    }
}