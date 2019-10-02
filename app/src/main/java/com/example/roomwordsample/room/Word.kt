package com.example.roomwordsample.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// https://codelabs.developers.google.com/codelabs/android-room-with-a-view-kotlin/#3
// https://developer.android.com/training/data-storage/room/defining-data.html
// Room Annotations - https://developer.android.com/reference/android/arch/persistence/room/package-summary.html

@Entity(tableName = "word_table")
data class Word(
        @ColumnInfo(name = "word") val word: String,
        @ColumnInfo(name = "datetimestamp") val datetimestamp: Long
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

