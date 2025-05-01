package com.example.to_dolist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolist.R
import com.example.to_dolist.data.local.NoteModel
import com.example.to_dolist.databinding.NoteListItemBinding

class NoteAdapter(
    val onBookmarkClick: (NoteModel) -> Unit,
    val onNoteClick: (NoteModel) -> Unit,
    val isTrash: Boolean = false,
) : ListAdapter<NoteModel, NoteAdapter.NoteVH>(NoteDiffCallback()) {

    fun getNoteAtPosition(position: Int): NoteModel = getItem(position)

    inner class NoteVH(val binding: NoteListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteModel) = binding.apply {
            titleTxt.text = note.title
            contentTxt.text = note.content
            favBtn.setImageResource(if (note.isBookmarked) R.drawable.ic_star_filed else R.drawable.ic_star_border)

            favBtn.setOnClickListener { onBookmarkClick(note) }
            card.setOnClickListener { onNoteClick(note) }

            favBtn.visibility = if (isTrash) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NoteVH(NoteListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: NoteVH, position: Int) {
        holder.bind(getItem(position))
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<NoteModel>() {
        override fun areItemsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem == newItem
        }
    }
}