package ru.androidlearning.theuniversearoundus.ui.notes

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.androidlearning.theuniversearoundus.R
import ru.androidlearning.theuniversearoundus.model.db.data_sources.db_entities.NoteEntity
import kotlin.math.max
import kotlin.math.min

class NotesRecyclerViewAdapter(
    private val onNoteClickListener: OnNoteClickListener,
    private val onDeleteClickListener: OnDeleteClickListener,
    private val onChangeOrderNumbers: OnChangeOrderNumbers,
    private val onStartDragListener: OnStartDragListener,
    private val onHighPriorityCheckBoxClickListener: OnHighPriorityCheckBoxClickListener,
    private var notesList: MutableList<NoteEntity> = mutableListOf()
) : RecyclerView.Adapter<NotesRecyclerViewAdapter.NotesViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.notes_fragment_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(notesList[position])
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    fun setNotesList(notesList: List<NoteEntity>) {
        this.notesList.clear()
        this.notesList.addAll(notesList)
        notifyDataSetChanged()
    }

    fun addNote(currentNote: NoteEntity) {
        notesList.add(currentNote)
        notifyItemInserted(notesList.indexOf(currentNote))
    }

    fun deleteNote(currentNote: NoteEntity) {
        val indexOfNote = notesList.indexOf(currentNote)
        notesList.remove(currentNote)
        notifyItemRemoved(indexOfNote)
    }

    fun updateNote(currentNote: NoteEntity) {
        notifyItemChanged(notesList.indexOf(currentNote))
    }

    fun getNotesList() = notesList

    fun itemMove(fromPosition: Int, toPosition: Int) {
        notesList.removeAt(min(fromPosition, toPosition)).let {
            notesList.add(max(fromPosition, toPosition), it)
        }
        notifyItemMoved(fromPosition, toPosition)
        onChangeOrderNumbers.changeOrderNumbers()
    }

    interface OnNoteClickListener {
        fun onClick(noteEntity: NoteEntity)
    }

    interface OnDeleteClickListener {
        fun onClick(noteEntity: NoteEntity)
    }

    interface OnChangeOrderNumbers {
        fun changeOrderNumbers()
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: NotesViewHolder)
    }

    interface OnHighPriorityCheckBoxClickListener {
        fun onClick(noteEntity: NoteEntity, isChecked: Boolean)
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ClickableViewAccessibility")
        fun bind(noteEntity: NoteEntity) {
            itemView.findViewById<TextView>(R.id.noteTextView).text = noteEntity.noteText
            itemView.findViewById<TextView>(R.id.creationDateTextView).text = noteEntity.creationDate
            itemView.findViewById<CheckBox>(R.id.highPriorityCheckBox).isChecked = (noteEntity.highPriority == true)
            itemView.findViewById<TextView>(R.id.noteTextView).setOnClickListener { onNoteClickListener.onClick(noteEntity) }
            itemView.findViewById<ImageView>(R.id.removeItemImageView).setOnClickListener { onDeleteClickListener.onClick(noteEntity) }
            itemView.findViewById<ImageView>(R.id.moveItemUp).setOnClickListener { moveUp() }
            itemView.findViewById<ImageView>(R.id.moveItemDown).setOnClickListener { moveDown() }
            itemView.findViewById<CheckBox>(R.id.highPriorityCheckBox)
                .setOnCheckedChangeListener { _, isChecked -> onHighPriorityCheckBoxClickListener.onClick(noteEntity, isChecked) }
            itemView.findViewById<ImageView>(R.id.dragHandleImageView).setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        onStartDragListener.onStartDrag(this)
                    }
                    MotionEvent.ACTION_UP -> view.performClick()
                    else -> {
                    }
                }
                true
            }
        }

        private fun moveUp() {
            layoutPosition.takeIf { it > 0 }?.let { itemMove(it, it - 1) }
        }

        private fun moveDown() {
            layoutPosition.takeIf { it < notesList.size - 1 }?.let { itemMove(it, it + 1) }
        }

        fun itemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        fun itemClear() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }
}