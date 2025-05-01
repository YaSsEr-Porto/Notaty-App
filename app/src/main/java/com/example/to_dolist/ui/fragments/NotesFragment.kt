package com.example.to_dolist.ui.fragments

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolist.adapters.NoteAdapter
import com.example.to_dolist.viewmodel.NoteViewModel
import com.example.to_dolist.R
import com.example.to_dolist.data.local.NoteModel
import com.example.to_dolist.databinding.FragmentNotesBinding
import com.example.to_dolist.ui.activities.AddNoteActivity
import com.example.to_dolist.ui.activities.MainActivity
import com.google.android.material.snackbar.Snackbar

class NotesFragment : Fragment() {

    private lateinit var binding: FragmentNotesBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteViewModel: NoteViewModel
    private var fullNoteList = listOf<NoteModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentNotesBinding.inflate(inflater, container, false)
        noteViewModel = ViewModelProvider(requireActivity())[NoteViewModel::class.java]

        noteAdapter = NoteAdapter(onBookmarkClick = { noteViewModel.toggleBookmark(it) }, onNoteClick = {
            startActivity(Intent(requireContext(), AddNoteActivity::class.java).apply {
                putExtra("note", it)
            })
        })

        binding.noteRv.adapter = noteAdapter

        noteViewModel.noteList.observe(viewLifecycleOwner) { notes ->
            fullNoteList = notes
            noteAdapter.submitList(fullNoteList)
            binding.noteTemp.visibility = if (fullNoteList.isEmpty()) View.VISIBLE else View.GONE
        }

        (activity as? MainActivity)?.binding?.addTaskBtn?.setOnClickListener {
            startActivity(Intent(requireContext(), AddNoteActivity::class.java))
        }

        swipeToDelete()

        return binding.root
    }

    fun filterNotes(query: String) {
        val filteredList = fullNoteList.filter { it.title.contains(query, ignoreCase = true) }
        noteAdapter.submitList(filteredList)
    }

    private fun swipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note = noteAdapter.getNoteAtPosition(viewHolder.adapterPosition)
                noteViewModel.moveToTrash(note)
                Snackbar.make(binding.root, "Note moved to Trash", Snackbar.LENGTH_LONG).setAction("Undo") {
                    noteViewModel.restoreFromTrash(note)
                }.show()
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean,
            ) {
                val itemView = viewHolder.itemView
                if (dX > 0) {
                    val background = Paint().apply {
                        color = "#B71C1C".toColorInt()
                        isAntiAlias = true
                    }
                    val rect = RectF(itemView.left.toFloat(), itemView.top.toFloat(), itemView.left + dX, itemView.bottom.toFloat())
                    c.drawRoundRect(rect, 46f, 46f, background)

                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_trash)?.let { icon ->
                        val margin = (itemView.height - icon.intrinsicHeight) / 2
                        icon.setBounds(
                            itemView.left + margin,
                            itemView.top + margin,
                            itemView.left + margin + icon.intrinsicWidth,
                            itemView.top + margin + icon.intrinsicHeight
                        )
                        icon.draw(c)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.noteRv)
    }
}