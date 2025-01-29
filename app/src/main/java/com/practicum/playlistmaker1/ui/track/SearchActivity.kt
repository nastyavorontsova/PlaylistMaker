package com.practicum.playlistmaker1.ui.track

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker1.Creator
import com.practicum.playlistmaker1.ui.audioplayer.AudioPlayerActivity
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker1.domain.api.TrackInteractor
import com.practicum.playlistmaker1.domain.models.Track

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val SEARCH_TEXT_KEY = "search_text"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val trackInteractor: TrackInteractor = Creator.provideTrackInteractor()
    private val searchHistoryInteractor: SearchHistoryInteractor = Creator.provideSearchHistoryInteractor()


    private lateinit var inputEditText: EditText
    private var searchText: String = ""

    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var emptyStateLayout: FrameLayout
    private lateinit var errorStateLayout: FrameLayout
    private lateinit var progressBar: FrameLayout

    private lateinit var retryButton: Button

    private var trackList: MutableList<Track> = mutableListOf()

    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyContainer: ViewGroup
    private lateinit var clearHistoryButton: Button

    private lateinit var historyAdapter: TrackAdapter

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        performSearch(searchText)
    }

    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        initHistory()

        val buttonSearchArrowBack: ImageView = findViewById(R.id.search_arrow_back)

        buttonSearchArrowBack.setOnClickListener {
            finish()
        }

        inputEditText = findViewById(R.id.inputEditText)
        trackRecyclerView = findViewById(R.id.trackRecyclerView)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        errorStateLayout = findViewById(R.id.errorStateLayout)
        progressBar = findViewById(R.id.progressBar)

        retryButton = findViewById(R.id.retryButton)

        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        trackAdapter = TrackAdapter(trackList) { track ->
            searchHistoryInteractor.addTrack(track)
            if (clickDebounce()){
                val intent = Intent(this, AudioPlayerActivity::class.java)
                intent.putExtra("TRACK_DATA", track)
                startActivity(intent)
            }
        }

        trackRecyclerView.adapter = trackAdapter

        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        historyContainer = findViewById(R.id.historyContainer)

        val clearButton: ImageView = findViewById(R.id.clearIcon)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard(it)

            handler.removeCallbacks(searchRunnable)
            trackList.clear()
            trackAdapter.notifyDataSetChanged()

            emptyStateLayout.visibility = View.GONE
            errorStateLayout.visibility = View.GONE

            updateHistoryVisibility(inputEditText.hasFocus())
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                clearButton.visibility = clearButtonVisibility(s)
                handleTextChange(searchText)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchDebounce()
                true
            } else {
                false
            }
        }

        retryButton.setOnClickListener {
            performSearch(searchText)
        }

        savedInstanceState?.let {
            searchText = it.getString(SEARCH_TEXT_KEY, "")
            inputEditText.setText(searchText)
        }

        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            updateHistoryVisibility(inputEditText.hasFocus())
        }
    }

    private fun performSearch(term: String) {
        if (term.isEmpty()) {
            Log.d("SearchActivity", "performSearch skipped for empty term")
            return
        }

        Log.d("SearchActivity", "performSearch called with term: '$term'")

        showLoading()

        trackInteractor.search(term, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>) {
                runOnUiThread {
                    hideLoading()
                    if (foundTracks.isNotEmpty()) {
                        trackList.clear()
                        trackList.addAll(foundTracks)
                        trackRecyclerView.visibility = View.VISIBLE
                        trackAdapter.notifyDataSetChanged()
                    } else {
                        showEmptyState()
                    }
                }
            }
        }, object : TrackInteractor.ErrorConsumer {
            override fun onError() {
                runOnUiThread {
                    hideLoading()
                    showErrorState()
                }
            }
        })
    }


    private fun showEmptyState() {
        trackRecyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.VISIBLE
        errorStateLayout.visibility = View.GONE
        historyContainer.visibility = View.GONE
    }

    private fun showErrorState() {
        trackRecyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
        errorStateLayout.visibility = View.VISIBLE
        historyContainer.visibility = View.GONE
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun updateHistoryVisibility(hasFocus: Boolean) {
        val history = searchHistoryInteractor.getHistory()

        if (hasFocus && history.isNotEmpty() && inputEditText.text.isEmpty()) {
            historyAdapter.updateData(history)
            historyContainer.visibility = View.VISIBLE
        } else {
            historyContainer.visibility = View.GONE
        }
    }

    private fun initViews() {
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        inputEditText = findViewById(R.id.inputEditText)
        historyContainer = findViewById(R.id.historyContainer)

        inputEditText.setOnClickListener {
            if (inputEditText.text.isEmpty()) {
                showSearchHistoryIfNotEmpty()
            } else {
                historyContainer.visibility = View.GONE
            }
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                showSearchHistoryIfNotEmpty()
            } else {
                historyContainer.visibility = View.GONE
            }
        }
    }

    private fun initHistory() {
        historyAdapter = TrackAdapter(searchHistoryInteractor.getHistory().toMutableList()) { track ->
            if (clickDebounce()){
                searchHistoryInteractor.addTrack(track)
                val intent = Intent(this, AudioPlayerActivity::class.java)
                intent.putExtra("TRACK_DATA", track)
                startActivity(intent)
            }
        }
        historyRecyclerView.adapter = historyAdapter
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        if (searchText.isNotEmpty()) {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
            Log.d("SearchActivity", "Debounce set for term: '$searchText'")
        } else {
            Log.d("SearchActivity", "Debounce skipped for empty term")
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        trackRecyclerView.visibility = View.GONE
        historyContainer.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
        errorStateLayout.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun handleTextChange(query: String) {
        if (query.isEmpty()) {
            handler.removeCallbacks(searchRunnable)
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            trackRecyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.GONE
            errorStateLayout.visibility = View.GONE

            val history = searchHistoryInteractor.getHistory()
            if (history.isNotEmpty()) {
                historyContainer.visibility = View.VISIBLE
                historyAdapter.updateData(history)
            } else {
                historyContainer.visibility = View.GONE
            }
        } else {
            searchDebounce()
        }
    }

    private fun showSearchHistoryIfNotEmpty() {
        val history = searchHistoryInteractor.getHistory()

        if (history.isNotEmpty()) {
            historyAdapter.updateData(history)
            historyContainer.visibility = View.VISIBLE
            trackRecyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.GONE
            errorStateLayout.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        inputEditText.setText(searchText)
    }
}
