package com.example.to_dolist.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "notes")
data class NoteModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var title: String,
    var content: String,
    var isBookmarked: Boolean = false,
    var timestamp: Long = System.currentTimeMillis(),
    var isTrashed: Boolean = false,
) : Parcelable