package com.akichiy.auffcardholder.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SwitchPinnedStatusUseCase @Inject constructor(
    private val repository: CardsRepository
) {

    suspend operator fun invoke(cardId: Int) {
        repository.switchPinStatus(cardId)
    }
}