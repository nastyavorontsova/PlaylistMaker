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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.player.ui.AudioPlayerActivity
import com.practicum.playlistmaker1.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModel()


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


        // Инициализация View элементов
        initViews()

        // Инициализация адаптеров
        initAdapters()

        // Загрузка истории при создании Activity
//        showEmptyQuery()

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
            viewModel.search("")
            hideKeyboard(it)
            showEmptyQuery()
        }

        // Обработка ввода текста
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                val query = s?.toString() ?: ""
                viewModel.search(query)

                // Если текст очищен, показываем историю (если есть фокус)
                if (query.isEmpty() && inputEditText.hasFocus()) {
                    viewModel.loadHistory()
                }
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
            if (hasFocus && inputEditText.text.isNullOrEmpty()) {
                // Если поле получило фокус и пустое, показываем историю
                viewModel.loadHistory()
            } else {
                // Иначе обновляем UI в зависимости от текущего состояния
                observeViewModel()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        // При возврате на экран проверяем состояние поля ввода
        if (inputEditText.text.isNullOrEmpty() && inputEditText.hasFocus()) {
            viewModel.loadHistory()
        } else {
            // Показываем последние результаты поиска, если они есть
            val lastResults = viewModel.getLastSearchResults()
            if (lastResults.isNotEmpty()) {
                showContent(lastResults, emptyList(), isHistoryVisible = false)
            } else {
                observeViewModel()
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
        viewModel.screenState.observe(this) { state ->
            when (state) {
                is SearchScreenState.Content -> {
                    showContent(state.tracks, state.history, state.isHistoryVisible)
                }
                SearchScreenState.Loading -> showLoading()
                SearchScreenState.EmptyQuery -> showEmptyQuery()
                SearchScreenState.EmptyResult -> showEmptyResult()
                SearchScreenState.Error -> showError()
            }
        }
    }

    private fun showContent(tracks: List<Track>, history: List<Track>, isHistoryVisible: Boolean) {
        val isInputEmpty = inputEditText.text.isNullOrEmpty()
        val hasFocus = inputEditText.hasFocus()

        if (isInputEmpty && hasFocus && history.isNotEmpty()) {
            // Показываем историю, если поле пустое, имеет фокус и есть треки в истории
            historyAdapter.updateData(history)
            historyContainer.visibility = View.VISIBLE
            trackRecyclerView.visibility = View.GONE
        } else {
            // Показываем результаты поиска
            trackAdapter.updateData(tracks)
            trackRecyclerView.visibility = View.VISIBLE
            historyContainer.visibility = View.GONE
        }

        progressBar.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
        errorStateLayout.visibility = View.GONE
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
        val history = (viewModel.screenState.value as? SearchScreenState.Content)?.history ?: emptyList()
        if (history.isNotEmpty()) {
            historyContainer.visibility = View.VISIBLE
            historyAdapter.updateData(history)
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