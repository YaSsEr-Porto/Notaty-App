package com.example.to_dolist.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface NoteDao {
    @Upsert
    suspend fun upsertNote(note: NoteModel)

    @Query("SELECT * FROM notes Where isTrashed = 0")
    fun getNotes(): LiveData<List<NoteModel>>

    @Query("SELECT * FROM notes Where isTrashed = 0 ORDER BY title ASC")
    fun getNotesSortedByTitle(): LiveData<List<NoteModel>>

    @Query("SELECT * FROM notes Where isTrashed = 0 ORDER BY timestamp DESC")
    fun getNotesSortedByDate(): LiveData<List<NoteModel>>

    @Query("SELECT * FROM notes Where isTrashed = 0 AND isBookmarked = 1")
    fun getBookmarkedNotes(): LiveData<List<NoteModel>>

    @Query("SELECT * FROM notes Where isTrashed = 1")
    fun getTrashedNotes(): LiveData<List<NoteModel>>

    @Delete
    suspend fun deleteNote(note: NoteModel)

    @Query("DELETE FROM notes")
    suspend fun clearAllNotes()

}