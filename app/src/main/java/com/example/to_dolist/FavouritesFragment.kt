package com.example.to_dolist

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolist.databinding.FragmentFavouritesBinding
import com.google.android.material.snackbar.Snackbar

class FavouritesFragment() : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var favAdapter: NoteAdapter
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel = ViewModelProvider(requireActivity())[NoteViewModel::class.java]

        favAdapter = NoteAdapter(arrayListOf(),
            onBookmarkClick = { position -> handleBookmark(position) },
            onNoteClick = { note, position -> handleNoteClick(note, position) })

        binding.favRv.adapter = favAdapter

        noteViewModel.noteList.observe(viewLifecycleOwner) { notes ->
            val favNotes = notes.filter { it.isBookmarked }
            favAdapter.updateList(favNotes)
            binding.favTemp.visibility = if (favNotes.isEmpty()) View.VISIBLE else View.GONE
        }

        swipeToDelete()
    }

    fun filterNotes(query: String) {
        val filteredNotes = noteViewModel.noteList.value?.filter { it.isBookmarked && it.title.contains(query, ignoreCase = true) } ?: emptyList()
        favAdapter.updateList(filteredNotes)
    }

    private fun handleBookmark(position: Int) {
        val favList = favAdapter.noteList
        if (position in favList.indices) {
            val note = favList[position]
            note.isBookmarked = false

            val fullList = noteViewModel.noteList.value ?: return
            val actualPosition = fullList.indexOfFirst { it.title == note.title }

            if (actualPosition != -1) noteViewModel.updateNote(actualPosition, note)
        }
    }

    private fun handleNoteClick(note: NoteModel, position: Int) {
        val i = Intent(requireContext(), AddNoteActivity::class.java).apply {
            putExtra("noteTitle", note.title)
            putExtra("noteContent", note.content)
            putExtra("position", position)
        }
        startActivity(i)
    }

    private fun swipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition //Save the position of the note.
                val deletedNote = favAdapter.noteList[position] //Save the deleted note.

                val fullList = noteViewModel.noteList.value ?: return
                val actualPosition = fullList.indexOfFirst { it.title == deletedNote.title }
                if (actualPosition != -1) {
                    noteViewModel.deleteNote(actualPosition)

                    // Show a Snackbar with an Undo.
                    Snackbar.make(binding.root, "Note deleted", Snackbar.LENGTH_LONG).setAction("Undo") {
                        noteViewModel.addNote(deletedNote)
                    }.show()
                }
            }

            val backgroundPaint = Paint().apply {
                color = Color.parseColor("#B71C1C")
                isAntiAlias = true
            }
            val cornerRadius = 46f
            val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_trash)

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean,
            ) {
                val itemView = viewHolder.itemView
                if (dX > 0) {
                    val backgroundRect = RectF(itemView.left.toFloat(), itemView.top.toFloat(), itemView.left + dX, itemView.bottom.toFloat())
                    c.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint)

                    // Draw the delete icon
                    val iconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2
                    val iconTop = itemView.top + iconMargin
                    val iconBottom = iconTop + deleteIcon.intrinsicHeight
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = iconLeft + deleteIcon.intrinsicWidth

                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    deleteIcon.draw(c)
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.favRv) // Attach the ItemTouchHelper to the RecyclerView.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}