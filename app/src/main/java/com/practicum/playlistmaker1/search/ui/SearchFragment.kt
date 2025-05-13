package com.practicum.playlistmaker1.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.databinding.FragmentSearchBinding
import com.practicum.playlistmaker1.player.ui.AudioPlayerFragment
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel by viewModel<SearchViewModel>()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var isClickAllowed = true
    private var debounceJob: Job? = null

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance() = SearchFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setKeyboardAdjustMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        initViews()
        initAdapters()

        if (savedInstanceState != null) {
            val searchQuery = savedInstanceState.getString("searchQuery", "")
            val searchResults = savedInstanceState.getParcelableArrayList<Track>("searchResults")

            if (!searchQuery.isNullOrEmpty() && searchResults != null) {
                binding.inputEditText.setText(searchQuery)
                viewModel.setLastSearchResults(searchResults)
                showContent(searchResults, emptyList(), isHistoryVisible = false)
            } else {
                viewModel.loadHistory()
            }
        } else {
            viewModel.loadHistory()
        }

        observeViewModel()

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            viewModel.search("")
            hideKeyboard(it)
            showEmptyQuery()
        }

        binding.inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                val query = s?.toString() ?: ""
                viewModel.search(query)

                if (query.isEmpty() && binding.inputEditText.hasFocus()) {
                    viewModel.loadHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.retryButton.setOnClickListener {
            viewModel.search(binding.inputEditText.text.toString(), isRetry = true)
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputEditText.text.isNullOrEmpty()) {
                viewModel.loadHistory()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("searchQuery", binding.inputEditText.text.toString())
        outState.putParcelableArrayList("searchResults", ArrayList(viewModel.getLastSearchResults()))
    }

    override fun onResume() {
        super.onResume()
        if (binding.inputEditText.text.isNullOrEmpty() && binding.inputEditText.hasFocus()) {
            viewModel.loadHistory()
        } else {
            val lastResults = viewModel.getLastSearchResults()
            if (lastResults.isNotEmpty()) {
                showContent(lastResults, emptyList(), isHistoryVisible = false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        debounceJob?.cancel()
        _binding = null
    }

    private fun initViews() {
        binding.trackRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initAdapters() {
        trackAdapter = TrackAdapter { track ->
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
                navigateToPlayer(track)
            }
        }

        historyAdapter = TrackAdapter { track ->
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
                navigateToPlayer(track)
            }
        }

        binding.trackRecyclerView.adapter = trackAdapter
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { state ->
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
    }

    private fun showContent(tracks: List<Track>, history: List<Track>, isHistoryVisible: Boolean) {
        val isInputEmpty = binding.inputEditText.text.isNullOrEmpty()
        val hasFocus = binding.inputEditText.hasFocus()

        if (isInputEmpty && hasFocus && history.isNotEmpty()) {
            historyAdapter.updateData(history)
            binding.historyContainer.visibility = View.VISIBLE
            binding.trackRecyclerView.visibility = View.GONE
        } else {
            trackAdapter.updateData(tracks)
            binding.trackRecyclerView.visibility = View.VISIBLE
            binding.historyContainer.visibility = View.GONE
        }

        binding.progressBar.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
        binding.errorStateLayout.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.trackRecyclerView.visibility = View.GONE
        binding.historyContainer.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
        binding.errorStateLayout.visibility = View.GONE
    }

    private fun showEmptyQuery() {
        binding.progressBar.visibility = View.GONE
        binding.trackRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
        binding.errorStateLayout.visibility = View.GONE

        val history = (viewModel.screenState.value?.peekContent() as? SearchScreenState.Content)?.history ?: emptyList()
        if (history.isNotEmpty()) {
            binding.historyContainer.visibility = View.VISIBLE
            historyAdapter.updateData(history)
        } else {
            binding.historyContainer.visibility = View.GONE
        }
    }

    private fun showEmptyResult() {
        binding.progressBar.visibility = View.GONE
        binding.trackRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.errorStateLayout.visibility = View.GONE
        binding.historyContainer.visibility = View.GONE
    }

    private fun showError() {
        binding.progressBar.visibility = View.GONE
        binding.trackRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
        binding.errorStateLayout.visibility = View.VISIBLE
        binding.historyContainer.visibility = View.GONE
    }

    private fun navigateToPlayer(track: Track) {
        val bundle = bundleOf("TRACK_DATA" to track.copy())
        findNavController().navigate(R.id.action_searchFragment_to_audioPlayerFragment, bundle)
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            debounceJob?.cancel()
            debounceJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun Fragment.setKeyboardAdjustMode(mode: Int) {
        activity?.window?.setSoftInputMode(mode)
    }
}

