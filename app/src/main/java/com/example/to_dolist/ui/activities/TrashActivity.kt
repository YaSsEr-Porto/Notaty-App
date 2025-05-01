package com.example.to_dolist.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.to_dolist.adapters.NoteAdapter
import com.example.to_dolist.viewmodel.NoteViewModel
import com.example.to_dolist.R
import com.example.to_dolist.databinding.ActivityTrashBinding

class TrashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrashBinding
    private lateinit var trashAdapter: NoteAdapter
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTrashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.trash)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        trashAdapter = NoteAdapter(onBookmarkClick = {}, onNoteClick = { note ->

            // Restore? or Delete Forever?
            val optionsMenu = arrayOf("Restore", "Delete Forever")
            AlertDialog.Builder(this).setItems(optionsMenu) { dialog, which ->
                when (which) {
                    0 -> {
                        noteViewModel.restoreFromTrash(note)
                        Toast.makeText(this, "Note Restored", Toast.LENGTH_SHORT).show()
                    }

                    1 -> {
                        noteViewModel.deleteNote(note)
                        Toast.makeText(this, "Note deleted Forever", Toast.LENGTH_SHORT).show()
                    }
                }
            }.show()
        })

        binding.trashRv.adapter = trashAdapter

        noteViewModel.trashList.observe(this) { trashList ->
            trashAdapter.submitList(trashList)
            binding.trashTemp.visibility = if (trashList.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.removeAllBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete All?")
                .setMessage("Are you Sure you want to permanently delete all trashed notes?")
                .setPositiveButton("Yes") { _, _ ->
                    val trashedNotes = noteViewModel.trashList.value ?: return@setPositiveButton
                    trashedNotes.forEach { noteViewModel.deleteNote(it) }
                    Toast.makeText(this, "All Notes Deleted Forever", Toast.LENGTH_SHORT).show() }
                .setNeutralButton("Cancel", null)
                .show()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@TrashActivity, MainActivity::class.java))
                finish()
            }
        })
    }
}