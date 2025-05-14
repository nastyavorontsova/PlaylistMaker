package com.practicum.playlistmaker1.media.playlist.ui

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Outline
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.caverock.androidsvg.SVG
import com.google.android.material.textfield.TextInputLayout
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.databinding.FragmentPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistViewModel by viewModel()
    private var selectedCoverUri: Uri? = null
    private var hasUnsavedChanges = false
    private var isFragmentActive = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFragmentActive = true

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                checkForUnsavedChanges()
            }
        })

        loadDefaultCover()
        setupTextWatchersAndFocusListeners()
        setupClickListeners()
        observeViewModel()
    }

    private fun loadDefaultCover() {
        try {
            val inputStream = requireContext().assets.open("empty_cover.svg")
            val svg = SVG.getFromInputStream(inputStream)
            val picture = svg.renderToPicture()

            binding.svgImageView.apply {
                setImageDrawable(PictureDrawable(picture))
                scaleType = ImageView.ScaleType.CENTER_CROP

                // Делаем обрезку углов
                clipToOutline = true
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        val radius = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            8f,
                            resources.displayMetrics
                        )
                        outline.setRoundRect(0, 0, view.width, view.height, radius)
                    }
                }

                // Убедимся, что обрезка применяется, когда view уже имеет размеры
                post { invalidateOutline() }
            }

            inputStream.close()
        } catch (e: Exception) {
            Log.e("PlaylistFragment", "Error loading default cover", e)
            binding.svgImageView.setImageResource(R.drawable.placeholder_cover)
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            selectedCoverUri = uri
            binding.svgImageView.setImageURI(uri)
            hasUnsavedChanges = true
        } ?: run {
            loadDefaultCover()
        }
    }

    private fun setupTextWatchersAndFocusListeners() {
        // Общий TextWatcher для обоих полей
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateInputLayoutAppearance()
                updateCreateButtonState(!binding.inputEditTextName.text.isNullOrBlank())
                hasUnsavedChanges = !binding.inputEditTextName.text.isNullOrEmpty() ||
                        !binding.inputEditTextDescription.text.isNullOrEmpty()
            }
        }

        // Обработчики фокуса для поля названия
        binding.inputEditTextName.apply {
            addTextChangedListener(textWatcher)
            setOnFocusChangeListener { _, hasFocus ->
                binding.inputLayoutName.isActivated = hasFocus || !text.isNullOrEmpty()
                updateInputLayoutAppearance()
                scrollToField(binding.inputLayoutName)
            }
        }

        // Обработчики фокуса для поля описания
        binding.inputEditTextDescription.apply {
            addTextChangedListener(textWatcher)
            setOnFocusChangeListener { _, hasFocus ->
                binding.inputLayoutDescription.isActivated = hasFocus || !text.isNullOrEmpty()
                updateInputLayoutAppearance()
                scrollToField(binding.inputLayoutDescription)
            }
        }
    }

    private fun scrollToField(view: View) {
        if (!isFragmentActive) return

        binding.scrollView.post {
            if (!isFragmentActive) return@post
            try {
                val scrollTo = view.bottom + binding.scrollView.scrollY
                binding.scrollView.smoothScrollTo(0, scrollTo)
            } catch (e: Exception) {
                Log.e("PlaylistFragment", "Scroll error", e)
            }
        }
    }

    private fun updateInputLayoutAppearance() {
        // Для поля названия
        updateInputLayoutState(
            binding.inputLayoutName,
            binding.inputEditTextName.hasFocus(),
            !binding.inputEditTextName.text.isNullOrEmpty()
        )

        // Для поля описания
        updateInputLayoutState(
            binding.inputLayoutDescription,
            binding.inputEditTextDescription.hasFocus(),
            !binding.inputEditTextDescription.text.isNullOrEmpty()
        )
    }

    private fun updateInputLayoutState(
        inputLayout: TextInputLayout,
        hasFocus: Boolean,
        hasText: Boolean
    ) {
        val isActive = hasFocus || hasText
        inputLayout.isActivated = isActive

        // Обновляем состояние для правильной работы селекторов
        inputLayout.refreshDrawableState()
    }

    private fun updateCreateButtonState(isEnabled: Boolean) {
        binding.createButton.isEnabled = isEnabled
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            checkForUnsavedChanges()
        }

        binding.svgImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createButton.setOnClickListener {
            val name = binding.inputEditTextName.text.toString()
            val description = binding.inputEditTextDescription.text?.toString()
            viewModel.createPlaylist(name, description, selectedCoverUri)
        }
    }

    private fun observeViewModel() {
        viewModel.playlistCreated.observe(viewLifecycleOwner) { success ->
            if (success) {
                val name = binding.inputEditTextName.text.toString()
                Toast.makeText(context, "Плейлист $name создан", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Ошибка при создании плейлиста", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.showExitDialog.observe(viewLifecycleOwner) { show ->
            if (show) {
                showExitDialog()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun checkForUnsavedChanges() {
        if (hasUnsavedChanges) {
            viewModel.checkForUnsavedChanges(true)
        } else {
            findNavController().popBackStack()
        }
    }

    private fun showExitDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setPositiveButton("Завершить") { _, _ ->
                isFragmentActive = false
                findNavController().popBackStack()
            }
            .setNegativeButton("Отмена", null)
            .setOnDismissListener {
                isFragmentActive = true
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isFragmentActive = false
        _binding = null
    }
}