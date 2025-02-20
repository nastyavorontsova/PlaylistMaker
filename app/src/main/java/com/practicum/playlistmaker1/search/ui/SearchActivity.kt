package com.practicum.playlistmaker1.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.creator.Creator
import com.practicum.playlistmaker1.player.ui.AudioPlayerActivity
import com.practicum.playlistmaker1.search.domain.models.Track

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var inputEditText: EditText
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var emptyStateLayout: FrameLayout
    private lateinit var errorStateLayout: FrameLayout
    private lateinit var progressBar: FrameLayout
    private lateinit var retryButton: Button
    private lateinit var historyContainer: ViewGroup
    private lateinit var clearHistoryButton: Button
    private lateinit var clearButton: ImageView

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val trackInteractor = Creator.provideTrackInteractor()
        val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()

        // Инициализация ViewModel
        val viewModelFactory = SearchViewModelFactory(trackInteractor, searchHistoryInteractor)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)

        // Инициализация View элементов
        initViews()

        // Инициализация адаптеров
        initAdapters()

        // Загрузка истории при создании Activity
        showEmptyQuery()

        // Наблюдение за изменениями в ViewModel
        observeViewModel()

        // Обработка нажатия на кнопку "Назад"
        val buttonSearchArrowBack: ImageView = findViewById(R.id.search_arrow_back)
        buttonSearchArrowBack.setOnClickListener {
            finish()
        }

        // Обработка нажатия на кнопку "Очистить"
        val clearButton: ImageView = findViewById(R.id.clearIcon)
        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard(it)
            viewModel.search("")
        }

        // Обработка ввода текста
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                val query = s?.toString() ?: ""
                viewModel.search(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Обработка нажатия на кнопку "Повторить"
        retryButton.setOnClickListener {
            viewModel.search(inputEditText.text.toString())
        }

        // Обработка нажатия на кнопку "Очистить историю"
        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("") // Очистка поля ввода
            viewModel.search("") // Очистка результатов поиска
            hideKeyboard(it) // Скрытие клавиатуры
        }

        // Обработка фокуса на поле ввода
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.loadHistory() // Загружаем историю при получении фокуса
            }
        }

    }

    private fun initViews() {
        inputEditText = findViewById(R.id.inputEditText)
        trackRecyclerView = findViewById(R.id.trackRecyclerView)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        errorStateLayout = findViewById(R.id.errorStateLayout)
        progressBar = findViewById(R.id.progressBar)
        retryButton = findViewById(R.id.retryButton)
        historyContainer = findViewById(R.id.historyContainer)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        clearButton = findViewById(R.id.clearIcon)

        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun initAdapters() {
        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
                navigateToPlayer(track)
            }
        }

        historyAdapter = TrackAdapter(mutableListOf()) { track ->
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
                navigateToPlayer(track)
            }
        }

        trackRecyclerView.adapter = trackAdapter
        historyRecyclerView.adapter = historyAdapter
    }

    private fun observeViewModel() {
        viewModel.searchState.observe(this, { state ->
            when (state) {
                is SearchViewModel.SearchState.Loading -> showLoading()
                is SearchViewModel.SearchState.EmptyQuery -> showEmptyQuery()
                is SearchViewModel.SearchState.EmptyResult -> showEmptyResult()
                is SearchViewModel.SearchState.Error -> showError()
                is SearchViewModel.SearchState.Success -> showTracks(state.tracks)
            }
        })

        viewModel.historyState.observe(this, { history ->
            historyAdapter.updateData(history)
            updateHistoryVisibility(history.isNotEmpty())
        })
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        trackRecyclerView.visibility = View.GONE
        historyContainer.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
        errorStateLayout.visibility = View.GONE
    }

    private fun showEmptyQuery() {
        progressBar.visibility = View.GONE
        trackRecyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
        errorStateLayout.visibility = View.GONE

        // Проверяем, есть ли треки в истории
        val history = viewModel.historyState.value ?: emptyList()
        if (history.isNotEmpty()) {
            historyContainer.visibility = View.VISIBLE
        } else {
            historyContainer.visibility = View.GONE
        }
    }

    private fun showEmptyResult() {
        progressBar.visibility = View.GONE
        trackRecyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.VISIBLE
        errorStateLayout.visibility = View.GONE
        historyContainer.visibility = View.GONE
    }

    private fun showError() {
        progressBar.visibility = View.GONE
        trackRecyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
        errorStateLayout.visibility = View.VISIBLE
        historyContainer.visibility = View.GONE
    }

    private fun showTracks(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        trackRecyclerView.visibility = View.VISIBLE
        emptyStateLayout.visibility = View.GONE
        errorStateLayout.visibility = View.GONE
        historyContainer.visibility = View.GONE
        trackAdapter.updateData(tracks)
    }

    private fun updateHistoryVisibility(hasHistory: Boolean) {
        val hasFocus = inputEditText.hasFocus()
        historyContainer.visibility = if (hasHistory && hasFocus) View.VISIBLE else View.GONE
    }

    private fun navigateToPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra("TRACK_DATA", track)
        startActivity(intent)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}