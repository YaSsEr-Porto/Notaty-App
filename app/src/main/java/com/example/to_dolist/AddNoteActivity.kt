package com.example.to_dolist

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.to_dolist.databinding.ActivityAddTaskBinding

class AddNoteActivity : AppCompatActivity() {

    var isEditing = false //To check if we are editing an existing note. Default value is false
    var notePosition = -1 //Default value.
    lateinit var binding: ActivityAddTaskBinding
    lateinit var noteTitle: String
    lateinit var noteContent: String

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

        /*
         - I'm Asking if the coming intent holds data with it.
         - If true that means I'm editing an existing note.
         - Then I fill the fields with the data I got from the intent.
         - Finally I save the note's position in the list.
         - So when I finish editing i send it back to the same position to prevent creating a new one.
         */
        intent?.let {
            if (it.hasExtra("noteTitle")) {
                isEditing = true
                binding.titleEt.setText(it.getStringExtra("noteTitle"))
                binding.contentEt.setText(it.getStringExtra("noteContent"))
                notePosition = it.getIntExtra("position", -1)
            }
        }

        binding.fabBtn.setOnClickListener {
            noteTitle = binding.titleEt.text.toString()
            noteContent = binding.contentEt.text.toString()

            if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                val resultIntent = Intent().apply {
                    putExtra("noteTitle", noteTitle)
                    putExtra("noteContent", noteContent)
                    putExtra("position", notePosition)
                }
                // Informs the main Screen that I have a new or edited note to update itself and show the changes.
                setResult(RESULT_OK, resultIntent)
                finish() // Close the activity.
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                noteTitle = binding.titleEt.text.toString()
                noteContent = binding.contentEt.text.toString()
                val exit = ExitDialog()

                if (noteTitle.isNotEmpty() || noteContent.isNotEmpty()) exit.show(
                    supportFragmentManager, "ExitDialog"
                )
                else finish()
            }
        })
    }
}