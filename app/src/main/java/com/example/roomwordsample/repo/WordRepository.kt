package com.example.roomwordsample.repo

import com.example.roomwordsample.room.Word
import com.example.roomwordsample.room.WordDao

class WordRepository(private val wordDao: WordDao) {
    val allWords = wordDao.getAllWords()

//    fun getAllWords() : Single<List<Word>> {
//        return wordDao.getAllWords()
//    }

    fun insert(word: Word) {
        wordDao.insert(word)
    }

    fun delete(word: Word) {
        wordDao.delete(word)
    }
}