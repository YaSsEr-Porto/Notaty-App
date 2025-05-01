package com.example.to_dolist.viewmodel

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.to_dolist.data.local.NoteDatabase
import com.example.to_dolist.data.local.NoteModel
import com.example.to_dolist.data.remote.CloudSyncManager
import com.example.to_dolist.data.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = NoteDatabase.Companion.getDatabase(application).noteDao()
    private val noteRepository = NoteRepository(noteDao)
    private val sortType = MutableLiveData<String>("date")
    val trashList = noteDao.getTrashedNotes()

    val noteList: LiveData<List<NoteModel>> = sortType.switchMap { type ->
        when (type) {
            "title" -> noteDao.getNotesSortedByTitle()
            else -> noteDao.getNotesSortedByDate()
        }
    }

    fun switchUserAndRestoreNotes(userId: String) = viewModelScope.launch {
        noteRepository.clearAllNotes()
        noteRepository.restoreNotesFromCloud(userId) { restoredNotes ->
            viewModelScope.launch {
                restoredNotes.forEach { note -> noteRepository.upsertNote(note) }
            }
        }
    }

    fun syncNotesNow(userId: String) {
        noteList.value?.let { notes -> CloudSyncManager.syncNotesToCloud(notes, userId) }
    }

    fun syncNotesToCloud(userId: String) = viewModelScope.launch { noteRepository.syncNotesToCloud(userId) }

    init {
        val savedSort = getSortOption(getApplication())
        sortType.value = savedSort
    }

    fun setSorting(type: String) {
        sortType.value = type
        saveSortOption(getApplication(), type)
    }

    fun upsertNote(note: NoteModel) = viewModelScope.launch {
        noteRepository.upsertNote(note)
    }

    fun deleteNote(note: NoteModel) = viewModelScope.launch {
        noteRepository.deleteNote(note)
    }

    fun toggleBookmark(note: NoteModel) = viewModelScope.launch {
        noteDao.upsertNote(note.copy(isBookmarked = !note.isBookmarked))
    }

    fun moveToTrash(note: NoteModel) = viewModelScope.launch {
        noteDao.upsertNote(note.copy(isTrashed = true))
    }

    fun restoreFromTrash(note: NoteModel) = viewModelScope.launch {
        noteDao.upsertNote(note.copy(isTrashed = false))
    }

    fun saveSortOption(context: Context, sortBy: String) {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit { putString("sort_by", sortBy) }
    }

    fun getSortOption(context: Context): String {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return prefs.getString("sort_by", "date") ?: "date"
    }
}