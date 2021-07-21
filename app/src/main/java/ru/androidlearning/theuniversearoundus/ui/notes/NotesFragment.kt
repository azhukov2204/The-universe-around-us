package ru.androidlearning.theuniversearoundus.ui.notes

import android.graphics.Canvas
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.androidlearning.theuniversearoundus.R
import ru.androidlearning.theuniversearoundus.databinding.NotesFragmentBinding
import ru.androidlearning.theuniversearoundus.model.DataChangeState
import ru.androidlearning.theuniversearoundus.model.DataLoadState
import ru.androidlearning.theuniversearoundus.model.db.data_sources.db_entities.NoteEntity
import ru.androidlearning.theuniversearoundus.ui.utils.showSnackBar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

private const val EMPTY_ID_ERROR_TEXT = "ID is empty"
private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
private const val TAG = "NotesFragment"

class NotesFragment : Fragment() {
    private val notesViewModel: NotesViewModel by lazy { ViewModelProvider(this).get(NotesViewModel::class.java) }
    private var _binding: NotesFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var notesRecyclerViewAdapter: NotesRecyclerViewAdapter
    private lateinit var currentNote: NoteEntity
    lateinit var itemTouchHelper: ItemTouchHelper
    private val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NotesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesRecyclerViewAdapter =
            NotesRecyclerViewAdapter(onNoteItemClickListener, onNoteDeleteClickListener, onChangeOrderNumbers, onStartDragListener, onHighPriorityCheckBoxClickListener)
        binding.notesRecyclerView.adapter = notesRecyclerViewAdapter
        binding.notesRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback()).apply { attachToRecyclerView(binding.notesRecyclerView) }
        notesViewModel.notesDataLoadingLiveData.observe(viewLifecycleOwner) { dataLoadState -> renderLoadingData(dataLoadState) }
        notesViewModel.noteDataChangingLiveData.observe(viewLifecycleOwner) { dataChangeState -> renderChangingData(dataChangeState) }
        if (savedInstanceState == null) {
            notesViewModel.getAllNotesFromDB()
        }
        binding.addNoteFAB.setOnClickListener(addNewNoteFABClickListener)
        binding.saveNoteButton.setOnClickListener(saveNoteClickListener)
    }

    private val onNoteItemClickListener = object : NotesRecyclerViewAdapter.OnNoteClickListener {
        override fun onClick(noteEntity: NoteEntity) {
            currentNote = noteEntity
            openNoteEditLayout()
        }
    }

    private val onNoteDeleteClickListener = object : NotesRecyclerViewAdapter.OnDeleteClickListener {
        override fun onClick(noteEntity: NoteEntity) {
            currentNote = noteEntity
            notesViewModel.deleteNoteInDB(currentNote)
        }
    }

    private val onStartDragListener = object : NotesRecyclerViewAdapter.OnStartDragListener {
        override fun onStartDrag(viewHolder: NotesRecyclerViewAdapter.NotesViewHolder) {
            itemTouchHelper.startDrag(viewHolder)
        }
    }

    private val onHighPriorityCheckBoxClickListener = object : NotesRecyclerViewAdapter.OnHighPriorityCheckBoxClickListener {
        override fun onClick(noteEntity: NoteEntity, isChecked: Boolean) {
            currentNote = noteEntity
            currentNote.highPriority = isChecked
            notesViewModel.updateNoteInDB(currentNote)
        }
    }

    private val onChangeOrderNumbers = object : NotesRecyclerViewAdapter.OnChangeOrderNumbers {
        override fun changeOrderNumbers() {
            updateOrderNumbersInDB()
        }
    }

    private val addNewNoteFABClickListener = OnClickListener {
        currentNote = NoteEntity()
        notesViewModel.insertNoteIntoDB(currentNote)
        openNoteEditLayout()
    }

    private val saveNoteClickListener = OnClickListener {
        val calendar = Calendar.getInstance()
        currentNote.noteText = binding.noteEditText.text.toString()
        currentNote.creationDate = simpleDateFormat.format(calendar.time)
        notesViewModel.updateNoteInDB(currentNote)
        closeNoteEditLayout()
    }

    private fun renderChangingData(dataChangeState: Pair<DataChangeState<NoteEntity>, DataManipulationTypes>) {
        when (dataChangeState.first) {
            is DataChangeState.Success -> {
                currentNote = (dataChangeState.first as DataChangeState.Success<NoteEntity>).responseData
                if (currentNote.id != null) {
                    when (dataChangeState.second) {
                        DataManipulationTypes.INSERT -> {
                            notesRecyclerViewAdapter.addNote(currentNote)
                        }
                        DataManipulationTypes.UPDATE -> {
                            notesRecyclerViewAdapter.updateNote(currentNote)
                        }
                        DataManipulationTypes.DELETE -> {
                            notesRecyclerViewAdapter.deleteNote(currentNote)
                        }
                    }
                    updateOrderNumbersInDB()
                } else {
                    showError(EMPTY_ID_ERROR_TEXT)
                    Log.d(TAG, EMPTY_ID_ERROR_TEXT)
                }
            }
            is DataChangeState.Error -> {
                showError((dataChangeState.first as DataChangeState.Error<NoteEntity>).error.message)
                Log.d(TAG, (dataChangeState.first as DataChangeState.Error<NoteEntity>).error.message.toString())
            }
        }
    }

    private fun renderLoadingData(dataLoadState: DataLoadState<List<NoteEntity>>?) {
        when (dataLoadState) {
            is DataLoadState.Success -> {
                notesRecyclerViewAdapter.setNotesList(dataLoadState.responseData)
            }
            is DataLoadState.Error -> {
                showError(dataLoadState.error.message)
                Log.d(TAG, dataLoadState.error.message.toString())
            }
            else -> {
            }
        }
    }

    private fun updateOrderNumbersInDB() {
        notesViewModel.updateOrderNumbersInDB(notesRecyclerViewAdapter.getNotesList())
    }

    private fun closeNoteEditLayout() {
        binding.addNoteFAB.visibility = View.VISIBLE
        binding.editNoteLayout.visibility = View.GONE
        binding.noteEditText.setText("")
    }

    private fun openNoteEditLayout() {
        binding.addNoteFAB.visibility = View.INVISIBLE
        binding.editNoteLayout.visibility = View.VISIBLE
        binding.noteEditText.setText(currentNote.noteText)
    }

    private fun showError(errorMessage: String?) {
        val message = "${getString(R.string.error_loading_data)}: $errorMessage"
        view?.showSnackBar(message)
    }

    inner class ItemTouchHelperCallback : ItemTouchHelper.Callback() {
        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            return true
        }

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            notesRecyclerViewAdapter.itemMove(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            currentNote = notesRecyclerViewAdapter.getNotesList()[viewHolder.adapterPosition]
            notesViewModel.deleteNoteInDB(currentNote)
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                val itemViewHolder = viewHolder as NotesRecyclerViewAdapter.NotesViewHolder
                itemViewHolder.itemSelected()
            }
            super.onSelectedChanged(viewHolder, actionState)
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            val itemViewHolder = viewHolder as NotesRecyclerViewAdapter.NotesViewHolder
            itemViewHolder.itemClear()
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                val width = viewHolder.itemView.width.toFloat()
                val alpha = 1.0f - abs(dX) / width
                viewHolder.itemView.alpha = alpha
                viewHolder.itemView.translationX = dX

            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
    }
}
