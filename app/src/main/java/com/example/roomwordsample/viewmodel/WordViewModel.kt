package com.example.roomwordsample.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.roomwordsample.repo.WordRepository
import com.example.roomwordsample.room.Word
import com.example.roomwordsample.room.WordRoomDatabase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

// https://codelabs.developers.google.com/codelabs/android-room-with-a-view-kotlin/#8
// modified to use RxJava instead of coroutines

// Class extends AndroidViewModel and requires application as a parameter.
class WordViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: WordRepository
    // Single gives us updated words when they change.
    val allWords: Observable<List<Word>>
    private val compositeDisposable = CompositeDisposable()

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct the correct WordRepository.
        val wordsDao = WordRoomDatabase.getDatabase(application).wordDao()
        repository = WordRepository(wordsDao)
        allWords = repository.allWords

    }

    // The implementation of insert() is completely hidden from the UI.
    // We don't want insert to block the main thread, so we're launching a new
    // coroutine. ViewModels have a coroutine scope based on their lifecycle called
    // viewModelScope which we can use here.
    fun insert(word: Word) {
        compositeDisposable.add(
                Completable.fromCallable { repository.insert(word) }
                .subscribeOn(Schedulers.io())
                .subscribe({ Log.e("MSW", "Inserted: $word") },
                           { Log.e("MSW", "Error inserting $word: ${it.message}") })
        )
    }

    fun cleanup() {
        compositeDisposable.dispose()
    }
}