package com.akichiy.auffcardholder.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.akichiy.auffcardholder.domain.ContentItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CardsDao {

    @Transaction
    @Query("SELECT * FROM cards ORDER BY updatedAt DESC")
    fun getAllCards(): Flow<List<CardWithContentDbModel>>

    @Transaction
    @Query("SELECT * FROM cards WHERE id == :cardId")
    suspend fun getCard(cardId: Int): CardWithContentDbModel

    @Transaction
    @Query("""
        SELECT DISTINCT cards.* FROM cards JOIN content ON cards.id == content.cardId 
        WHERE title LIKE '%' ||:query || '%' 
        OR content LIKE '%' ||:query || '%' 
        ORDER BY updatedAt DESC
        """)
    fun searchCards(query: String): Flow<List<CardWithContentDbModel>>

    @Transaction
    @Query("DELETE FROM cards WHERE id == :cardId")
    suspend fun deleteCard(cardId: Int)

    @Query("UPDATE cards SET isPinned = NOT isPinned WHERE id == :cardId")
    suspend fun switchPinnedStatus(cardId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCard(cardDbModel: CardDbModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCardContent(content: List<ContentItemDbModel>)


    @Query("DELETE FROM content WHERE cardId == :cardId")
    suspend fun deleteCardContent(cardId: Int)

    @Transaction
    suspend fun addCardWithContent(
        cardDbModel: CardDbModel,
        content: List<ContentItem>
    ) {
        val cardId = addCard(cardDbModel).toInt()
        val contentItems = content.toContentItemDbModels(cardId)
        addCardContent(contentItems)
    }

    @Transaction
    suspend fun updateCard(
        cardDbModel: CardDbModel,
        content: List<ContentItemDbModel>
    ) {
        addCard(cardDbModel)
        deleteCardContent(cardId = cardDbModel.id)
        addCardContent(content)
    }




}