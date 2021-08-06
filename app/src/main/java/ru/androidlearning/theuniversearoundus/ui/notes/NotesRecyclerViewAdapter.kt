package ru.androidlearning.theuniversearoundus.ui.notes

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import ru.androidlearning.theuniversearoundus.R
import ru.androidlearning.theuniversearoundus.model.db.data_sources.db_entities.NoteEntity
import kotlin.math.max
import kotlin.math.min

class NotesRecyclerViewAdapter(
    private val onNoteClickListener: OnNoteClickListener,
    private val onDeleteClickListener: OnDeleteClickListener,
    private val onChangeOrderNumbers: OnChangeOrderNumbers,
    private val onStartDragListener: OnStartDragListener,
    private val onHighPriorityCheckBoxClickListener: OnHighPriorityCheckBoxClickListener
) : RecyclerView.Adapter<NotesRecyclerViewAdapter.NotesViewHolder>() {
    private var notesList: MutableList<NoteEntity> = mutableListOf()
    private var filteredNotesList: MutableList<NoteEntity> = mutableListOf()
    private var filterQuery: String? = null
    private var filterByHighPriorityEnabled: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.notes_fragment_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(filteredNotesList[position])
    }

    override fun getItemCount(): Int {
        return filteredNotesList.size
    }

    fun setNotesList(notesList: List<NoteEntity>) {
        this.notesList.apply {
            clear()
            addAll(notesList)
        }
        applyFilter()
        notifyDataSetChanged()
    }

    fun addNote(currentNote: NoteEntity) {
        notesList.add(currentNote)
        applyFilter()
        filteredNotesList.indexOf(currentNote).takeIf { it >= 0 }?.let { notifyItemInserted(it) }
    }

    fun deleteNote(currentNote: NoteEntity) {
        val indexOfNote = filteredNotesList.indexOf(currentNote)
        notesList.remove(currentNote)
        applyFilter()
        notifyItemRemoved(indexOfNote)
    }

    fun updateNote(currentNote: NoteEntity) {
        notifyItemChanged(filteredNotesList.indexOf(currentNote))
    }

    fun getNotesList() = notesList

    fun getFilteredNotesList() = filteredNotesList

    fun itemMove(fromPosition: Int, toPosition: Int) {
        if (filterQuery.isNullOrBlank()) {
            notesList.removeAt(min(fromPosition, toPosition)).let {
                notesList.add(max(fromPosition, toPosition), it)
            }
            notifyItemMoved(fromPosition, toPosition)
            onChangeOrderNumbers.changeOrderNumbers()
        }
    }

    fun applyFilter(filterQuery: String? = this.filterQuery, callNotify: Boolean = false) {
        this.filterQuery = filterQuery
        filteredNotesList = (if (!filterQuery.isNullOrBlank()) {
            notesList.filter {
                (it.noteText?.lowercase()?.contains(filterQuery.lowercase()) == true) ||
                        (it.creationDate?.lowercase()?.contains(filterQuery.lowercase()) == true)
            }.toMutableList()
        } else {
            notesList
        })

        if (filterByHighPriorityEnabled) {
            filteredNotesList = filteredNotesList.filter {
                it.highPriority == true
            }.toMutableList()
        }

        if (callNotify) {
            notifyDataSetChanged()
        }
    }

    fun applyHighPriorityFilter(filterByHighPriorityEnabled: Boolean) {
        this.filterByHighPriorityEnabled = filterByHighPriorityEnabled
        applyFilter(callNotify = true)
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
        fun onClick(noteEntity: NoteEntity)
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ClickableViewAccessibility")
        fun bind(noteEntity: NoteEntity) {
            itemView.findViewById<ImageView>(R.id.removeItemImageView).setOnClickListener { onDeleteClickListener.onClick(noteEntity) }
            itemView.findViewById<TextView>(R.id.creationDateTextView).text = noteEntity.creationDate
            itemView.findViewById<TextView>(R.id.noteTextView).apply {
                text = noteEntity.noteText
                setOnClickListener { onNoteClickListener.onClick(noteEntity) }
            }
            itemView.findViewById<SwitchMaterial>(R.id.highPrioritySwitch).apply {
                setOnCheckedChangeListener(null)
                isChecked = (noteEntity.highPriority == true)
                setOnCheckedChangeListener { _, isChecked ->
                    noteEntity.highPriority = isChecked
                    onHighPriorityCheckBoxClickListener.onClick(noteEntity)
                }
            }
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

        fun itemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        fun itemClear() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }
}