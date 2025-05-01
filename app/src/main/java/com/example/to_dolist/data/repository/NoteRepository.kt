package com.example.to_dolist.data.repository

import androidx.lifecycle.LiveData
import com.example.to_dolist.data.remote.CloudSyncManager
import com.example.to_dolist.data.local.NoteDao
import com.example.to_dolist.data.local.NoteModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteRepository(private val noteDao: NoteDao) {

    // Local data source (Room).
    val allNotes: LiveData<List<NoteModel>> = noteDao.getNotes()

    suspend fun clearAllNotes() {
        noteDao.clearAllNotes()
    }

    // Sync Notes to Cloud (Firestore).
    suspend fun syncNotesToCloud(userId: String) {
        val localNotes = noteDao.getNotes().value ?: return
        CloudSyncManager.syncNotesToCloud(localNotes, userId)
    }

    // Restore Notes from Cloud (Firestore).
    fun restoreNotesFromCloud(userId: String, onSuccess: (List<NoteModel>) -> Unit) {
        CloudSyncManager.restoreNotesFromCloud(userId) { restoredNotes ->
            CoroutineScope(Dispatchers.IO).launch {
                restoredNotes.forEach { note ->
                    noteDao.upsertNote(note)
                }
                withContext(Dispatchers.Main) {
                    onSuccess(restoredNotes)
                }
            }
        }
    }

    suspend fun upsertNote(note: NoteModel) {
        noteDao.upsertNote(note)
    }

    suspend fun deleteNote(note: NoteModel) {
        noteDao.deleteNote(note)
    }
}