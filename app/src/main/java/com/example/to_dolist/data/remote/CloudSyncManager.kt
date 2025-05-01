package com.example.to_dolist.data.remote

import com.example.to_dolist.data.remote.FirestoreNote
import com.example.to_dolist.data.local.NoteModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object CloudSyncManager {

    private val firestore get() = Firebase.firestore

    fun syncNotesToCloud(notes: List<NoteModel>, userId: String) {
        val userNotesRef = firestore.collection("notes").document(userId).collection("userNotes")

        notes.forEach { note ->
            val fsNote = FirestoreNote(
                note.id.toString(), note.title, note.content, note.timestamp
            )
            userNotesRef.document(note.id.toString()).set(fsNote)
        }
    }

    fun restoreNotesFromCloud(userId: String, onSuccess: (List<NoteModel>) -> Unit) {
        val userNotesRef = firestore.collection("notes").document(userId).collection("userNotes")

        userNotesRef.get().addOnSuccessListener { documents ->
            val restoredNotes = documents.mapNotNull { doc ->
                doc.toObject(FirestoreNote::class.java).let { fsNote ->
                    NoteModel(
                        fsNote.id.toIntOrNull() ?: 0, fsNote.title, fsNote.content, false, fsNote.timestamp, false
                    )
                }
            }
            onSuccess(restoredNotes)
        }
    }
}