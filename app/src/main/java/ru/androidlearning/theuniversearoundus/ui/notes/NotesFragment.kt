package ru.androidlearning.theuniversearoundus.ui.notes

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import androidx.transition.Fade
import androidx.transition.TransitionManager
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
private const val ANIMATION_DURATION = 300L

class NotesFragment : Fragment() {
    private val notesViewModel: NotesViewModel by lazy { ViewModelProvider(this).get(NotesViewModel::class.java) }
    private var _binding: NotesFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var notesRecyclerViewAdapter: NotesRecyclerViewAdapter
    private var currentNote: NoteEntity? = null
    lateinit var itemTouchHelper: ItemTouchHelper
    private val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    private lateinit var appCompatActivity: AppCompatActivity
    private lateinit var menuItemActionSearchNotes: MenuItem
    private lateinit var menuItemFilterByHighPriorityCheckBox: MenuItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NotesFragmentBinding.inflate(inflater, container, false)
        appCompatActivity = activity as AppCompatActivity
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
        binding.notesRecyclerView.adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.addNoteFAB.setOnClickListener(addNewNoteFABClickListener)
        binding.noteEditText.addTextChangedListener(onNoteTextChangedListener)
        val itemAnimator = binding.notesRecyclerView.itemAnimator as SimpleItemAnimator
        itemAnimator.supportsChangeAnimations = false
        appCompatActivity.setSupportActionBar(binding.topAppBar)
        setHasOptionsMenu(true)

        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback()).apply { attachToRecyclerView(binding.notesRecyclerView) }

        notesViewModel.notesDataLoadingLiveData.observe(viewLifecycleOwner) { dataLoadState -> renderLoadingData(dataLoadState) }
        notesViewModel.noteDataChangingLiveData.observe(viewLifecycleOwner) { dataChangeState -> renderChangingData(dataChangeState) }
        if (savedInstanceState == null) {
            notesViewModel.getAllNotesFromDB()
        }
    }

    private val onNoteItemClickListener = object : NotesRecyclerViewAdapter.OnNoteClickListener {
        override fun onClick(noteEntity: NoteEntity) {
            currentNote = noteEntity
            openNoteEditLayout()
        }
    }

    private val onNoteDeleteClickListener = object : NotesRecyclerViewAdapter.OnDeleteClickListener {
        override fun onClick(noteEntity: NoteEntity) {
            currentNote = noteEntity.also {
                notesViewModel.deleteNoteInDB(it)
            }
        }
    }

    private val onStartDragListener = object : NotesRecyclerViewAdapter.OnStartDragListener {
        override fun onStartDrag(viewHolder: NotesRecyclerViewAdapter.NotesViewHolder) {
            itemTouchHelper.startDrag(viewHolder)
        }
    }

    private val onHighPriorityCheckBoxClickListener = object : NotesRecyclerViewAdapter.OnHighPriorityCheckBoxClickListener {
        override fun onClick(noteEntity: NoteEntity) {
            currentNote = noteEntity.also {
                notesViewModel.updateNoteInDB(it)
            }
        }
    }

    private val onChangeOrderNumbers = object : NotesRecyclerViewAdapter.OnChangeOrderNumbers {
        override fun changeOrderNumbers() {
            updateOrderNumbersInDB()
        }
    }

    private val addNewNoteFABClickListener = OnClickListener {
        currentNote = NoteEntity().also {
            notesViewModel.insertNoteIntoDB(it)
        }
    }

    private val onNoteTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            saveNote()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun renderChangingData(dataChangeState: Pair<DataChangeState<NoteEntity>, DataManipulationTypes>) {
        when (dataChangeState.first) {
            is DataChangeState.Success -> {
                currentNote = (dataChangeState.first as DataChangeState.Success<NoteEntity>).responseData
                if (currentNote?.id != null) {
                    when (dataChangeState.second) {
                        DataManipulationTypes.INSERT -> {
                            openNoteEditLayout()
                            currentNote?.let { notesRecyclerViewAdapter.addNote(it) }
                        }
                        DataManipulationTypes.UPDATE -> {
                            currentNote?.let { notesRecyclerViewAdapter.updateNote(it) }
                        }
                        DataManipulationTypes.DELETE -> {
                            currentNote?.let { notesRecyclerViewAdapter.deleteNote(it) }
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
        hideOrShowNoNotesTextView()
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
        hideOrShowNoNotesTextView()
    }

    private fun hideOrShowNoNotesTextView() {
        binding.noNotesTextView.visibility = if (notesRecyclerViewAdapter.getNotesList().size > 0) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hideOrShowNoFilteredResultsTextView() {
        binding.noFilteredResultsTextView.visibility = if (notesRecyclerViewAdapter.getFilteredNotesList().size > 0 || binding.noNotesTextView.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun updateOrderNumbersInDB() {
        notesViewModel.updateOrderNumbersInDB(notesRecyclerViewAdapter.getNotesList())
    }

    fun closeNoteEditLayout() {
        TransitionManager.beginDelayedTransition(binding.noteFragmentConstraintLayout, Fade().apply { duration = ANIMATION_DURATION })
        binding.addNoteFAB.show()
        binding.editNoteLayout.visibility = View.GONE
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        appCompatActivity.supportActionBar?.setHomeButtonEnabled(false)
        hideKeyboard()
        appCompatActivity.supportActionBar?.setTitle(R.string.notes_title)
        menuItemActionSearchNotes.isVisible = true
        menuItemFilterByHighPriorityCheckBox.isVisible = true
        isOpenedNoteEditLayout = false
    }

    private fun openNoteEditLayout() {
        TransitionManager.beginDelayedTransition(binding.noteFragmentConstraintLayout, Fade().apply { duration = ANIMATION_DURATION })
        binding.addNoteFAB.hide()
        binding.editNoteLayout.apply {
            visibility = View.VISIBLE
            requestFocus()
        }
        binding.noteEditText.setText(currentNote?.noteText)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appCompatActivity.supportActionBar?.setHomeButtonEnabled(true)
        showKeyboard()
        appCompatActivity.supportActionBar?.setTitle(R.string.edit_notes_title)
        menuItemActionSearchNotes.isVisible = false
        menuItemFilterByHighPriorityCheckBox.isVisible = false
        isOpenedNoteEditLayout = true
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.editNoteLayout.windowToken, 0)
    }

    private fun showKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.notes_menu, menu)
        menuItemActionSearchNotes = menu.findItem(R.id.action_search_notes).also {
            (it.actionView as SearchView).setOnQueryTextListener(searchNotesListener)
        }
        menuItemFilterByHighPriorityCheckBox = menu.findItem(R.id.filter_by_high_priority_check_box).also {
            (it.actionView as CheckBox).text = getString(R.string.high_priority_only_text)
            (it.actionView as CheckBox).setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            (it.actionView as CheckBox).setOnCheckedChangeListener { _, isChecked ->
                notesRecyclerViewAdapter.applyHighPriorityFilter(isChecked)
                hideOrShowNoFilteredResultsTextView()
            }
        }
    }

    private val searchNotesListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            notesRecyclerViewAdapter.applyFilter(query, true)
            hideOrShowNoFilteredResultsTextView()
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            notesRecyclerViewAdapter.applyFilter(newText, true)
            hideOrShowNoFilteredResultsTextView()
            return true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            saveNote()
            closeNoteEditLayout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNote() {
        val calendar = Calendar.getInstance()
        currentNote?.let {
            it.noteText = binding.noteEditText.text.toString()
            it.creationDate = simpleDateFormat.format(calendar.time)
            notesViewModel.updateNoteInDB(it)
        }
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
            notesRecyclerViewAdapter.itemMove(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            currentNote = notesRecyclerViewAdapter.getFilteredNotesList()[viewHolder.absoluteAdapterPosition].also {
                notesViewModel.deleteNoteInDB(it)
            }
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

    companion object {
        private var isOpenedNoteEditLayout: Boolean = false
        fun getIsOpenedNoteEditLayout() = isOpenedNoteEditLayout
    }
}
