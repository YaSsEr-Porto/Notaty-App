package com.example.to_dolist

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("app_prefs", MODE_PRIVATE)
    private val gson = Gson()

    private val _noteList = MutableLiveData<ArrayList<NoteModel>>(loadNots())
    val noteList: LiveData<ArrayList<NoteModel>> get() = _noteList

    private var currentSorting = ""

    fun setSorting(sorting: String) {
        if (currentSorting != sorting) {
            currentSorting = sorting
            _noteList.value = ArrayList(sortNotes(ArrayList(_noteList.value ?: arrayListOf())))
            saveNotes()
        }
    }

    fun addNote(note: NoteModel) {
        val updatedList = ArrayList(_noteList.value ?: arrayListOf()).apply {
            add(note)
        }
        _noteList.value = sortNotes(updatedList)
        saveNotes()
    }

    fun updateNote(position: Int, newNote: NoteModel) {
        val updatedList = ArrayList(_noteList.value ?: arrayListOf()).apply {
            this[position] = newNote
        }
        _noteList.value = sortNotes(updatedList)
        saveNotes()
    }

    fun deleteNote(position: Int) {
        val updatedList = ArrayList(_noteList.value ?: arrayListOf()).apply {
            removeAt(position)
        }
        _noteList.value = sortNotes(updatedList)
        saveNotes()
    }

    fun toggleBookmark(position: Int) {
        _noteList.value = _noteList.value?.apply {
            this[position].isBookmarked = !this[position].isBookmarked
            sortNotes(this)
            saveNotes()
        }
    }

    private fun sortNotes(notes: ArrayList<NoteModel>): ArrayList<NoteModel> {
        return when (currentSorting) {
            "title" -> ArrayList(notes.sortedBy { it.title.lowercase() })
            "date" -> ArrayList(notes.sortedByDescending { it.timestamp })
            else -> notes
        }
    }

    private fun saveNotes() {
        val json = gson.toJson(_noteList.value)
        prefs.edit().putString("saved_notes", json).apply()
    }

    private fun loadNots(): ArrayList<NoteModel> {
        val json = prefs.getString("saved_notes", null) ?: return arrayListOf()
        val type = object : TypeToken<ArrayList<NoteModel>>() {}.type
        return gson.fromJson(json, type)
    }
}