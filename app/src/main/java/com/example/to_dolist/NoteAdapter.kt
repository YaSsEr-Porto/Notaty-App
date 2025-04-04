package com.example.to_dolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolist.databinding.NoteListItemBinding

class NoteAdapter(
    val noteList: ArrayList<NoteModel>,
    val onBookmarkClick: (Int) -> Unit,
    val onNoteClick: (NoteModel, Int) -> Unit,
) : RecyclerView.Adapter<NoteAdapter.NoteVH>() {

    inner class NoteVH(val binding: NoteListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteModel, position: Int) = binding.apply {
            titleTxt.text = note.title
            contentTxt.text = note.content
            favBtn.setImageResource(if (note.isBookmarked) R.drawable.ic_star_filed else R.drawable.ic_star_border)

            favBtn.setOnClickListener { onBookmarkClick(position) }
            card.setOnClickListener { onNoteClick(note, position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NoteVH(NoteListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: NoteVH, position: Int) {
        holder.bind(noteList[position], position)
    }

    override fun getItemCount() = noteList.size

    fun updateList(newList: List<NoteModel>) {
        noteList.clear()
        noteList.addAll(newList)
        notifyDataSetChanged()
    }
}