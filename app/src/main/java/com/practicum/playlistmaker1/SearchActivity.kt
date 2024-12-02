package com.practicum.playlistmaker1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val SEARCH_TEXT_KEY = "search_text"
    }

    private lateinit var inputEditText: EditText
    private var searchText: String = ""

    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var emptyStateLayout: FrameLayout
    private lateinit var errorStateLayout: FrameLayout

    private lateinit var retryButton: Button

    private var trackList: MutableList<Track> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonSearchArrowBack: ImageView = findViewById(R.id.search_arrow_back)

        buttonSearchArrowBack.setOnClickListener {
            finish()
        }

        inputEditText = findViewById(R.id.inputEditText)
        trackRecyclerView = findViewById(R.id.trackRecyclerView)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        errorStateLayout = findViewById(R.id.errorStateLayout)

        retryButton = findViewById(R.id.retryButton)

        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(trackList)
        trackRecyclerView.adapter = trackAdapter

        val clearButton: ImageView = findViewById(R.id.clearIcon)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard(it)

            trackList.clear()
            trackAdapter.notifyDataSetChanged()

            emptyStateLayout.visibility = View.GONE
            errorStateLayout.visibility = View.GONE
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(inputEditText.text.toString())
                true
            } else {
                false
            }
        }

        inputEditText.setOnClickListener {
            inputEditText.text.clear()
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            trackRecyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.GONE
            errorStateLayout.visibility = View.GONE
        }

        retryButton.setOnClickListener {
            performSearch(inputEditText.text.toString())
        }

        savedInstanceState?.let {
            searchText = it.getString(SEARCH_TEXT_KEY, "")
            inputEditText.setText(searchText)
        }
    }

    private fun performSearch(term: String) {

        emptyStateLayout.visibility = View.GONE
        errorStateLayout.visibility = View.GONE

        val call = RetrofitClient.apiService.search(term)
        call.enqueue(object : Callback<SongResponse> {
            override fun onResponse(call: Call<SongResponse>, response: Response<SongResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val songResponse = response.body()!!
                    if (songResponse.resultCount > 0) {
                        trackList.clear()
                        trackList.addAll(songResponse.results)
                        trackRecyclerView.visibility = View.VISIBLE
                        trackAdapter.notifyDataSetChanged()
                    } else {
                        showEmptyState()
                    }
                } else {
                    showErrorState()
                }
            }

            override fun onFailure(call: Call<SongResponse>, t: Throwable) {
                showErrorState()
            }
        })
    }

    private fun showEmptyState() {
        trackRecyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.VISIBLE
        errorStateLayout.visibility = View.GONE
    }

    private fun showErrorState() {
        trackRecyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
        errorStateLayout.visibility = View.VISIBLE
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
