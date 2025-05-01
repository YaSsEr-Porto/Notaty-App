package com.example.to_dolist.ui.activities

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.to_dolist.utils.ExitDialog
import com.example.to_dolist.viewmodel.NoteViewModel
import com.example.to_dolist.R
import com.example.to_dolist.data.local.NoteModel
import com.example.to_dolist.databinding.ActivityAddTaskBinding

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var noteViewModel: NoteViewModel
    private var existingNote: NoteModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.con)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        existingNote = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.getParcelableExtra("note", NoteModel::class.java)
        else intent.getParcelableExtra("note")

        existingNote?.let {
            binding.titleEt.setText(it.title)
            binding.contentEt.setText(it.content)
        }

        binding.fabBtn.setOnClickListener {
            val title = binding.titleEt.text.toString()
            val content = binding.contentEt.text.toString()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                if (existingNote != null) {
                    val updatedNote = existingNote!!.copy(title = title, content = content)
                    noteViewModel.upsertNote(updatedNote)
                } else {
                    val newNote = NoteModel(title = title, content = content)
                    noteViewModel.upsertNote(newNote)
                }
                finish()
            } else Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT).show()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val hasText = binding.titleEt.text!!.isNotEmpty() || binding.contentEt.text!!.isNotEmpty()
                if (hasText) {
                    ExitDialog().show(supportFragmentManager, "ExitDialog")
                } else finish()
            }
        })
    }
}