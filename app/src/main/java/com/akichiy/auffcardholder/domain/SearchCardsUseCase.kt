package com.akichiy.auffcardholder.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchCardsUseCase @Inject constructor(
    private val repository: CardsRepository
) {

    operator fun invoke(query: String): Flow<List<Card>> {
       return repository.searchCards(query)
    }
}