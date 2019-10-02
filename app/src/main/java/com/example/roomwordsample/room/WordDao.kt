package com.example.roomwordsample.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Observable

// https://codelabs.developers.google.com/codelabs/android-room-with-a-view-kotlin/#4
// https://developer.android.com/training/data-storage/room/accessing-data

@Dao
interface WordDao {
    @Query("SELECT * from word_table ORDER BY word ASC")
    fun getAllWords(): Observable<List<Word>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(word: Word)

    @Update
    fun update(word: Word)

    @Delete
    fun delete(word: Word)

    @Query("DELETE FROM word_table") // @Query requires that you provide a SQL query as a string parameter to the annotation.
    fun deleteAll()

    @Query("SELECT * from word_table WHERE word like :word")
    fun getSimilarWords(word: String): List<Word>
}