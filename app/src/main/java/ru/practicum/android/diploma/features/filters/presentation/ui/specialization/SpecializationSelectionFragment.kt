package ru.practicum.android.diploma.features.filters.presentation.ui.specialization

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSpecializationSelectionBinding
import ru.practicum.android.diploma.features.common.presentation.ui.BaseFragment
import ru.practicum.android.diploma.features.filters.presentation.model.state.IndustriesState
import ru.practicum.android.diploma.features.filters.presentation.ui.specialization.adapter.SpecializationSelectionAdapter
import ru.practicum.android.diploma.utils.collectWithLifecycle
import ru.practicum.android.diploma.utils.debounce

class SpecializationSelectionFragment : BaseFragment<FragmentSpecializationSelectionBinding>() {

    private var specializationAdapter: SpecializationSelectionAdapter? = null
    private var onSearchDebounce: ((String) -> Unit)? = null
    private val viewModel by viewModel<SpecializationSelectionViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSpecializationSelectionBinding {
        return FragmentSpecializationSelectionBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getIndustries()
        viewModel.loadSavedIndustry()
    }

    override fun initUi() {
        initAdapter()
        initSearchDebounce()
        initListeners()
        initChooseButtonListener()
    }

    override fun observeData() {
        lifecycleScope.launch {
            viewModel.industriesState.collectWithLifecycle(
                this@SpecializationSelectionFragment
            ) { state ->
                renderState(state)
            }
        }

        lifecycleScope.launch {
            viewModel.savedSelectedIndustry.collectWithLifecycle(
                this@SpecializationSelectionFragment
            ) { selectedIndustry ->
                specializationAdapter?.updateItems(
                    (viewModel.industriesState.value as? IndustriesState.Content)?.industries ?: emptyList(),
                    selectedIndustry
                )
                updateChooseButtonVisibility(selectedIndustry != null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        specializationAdapter = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initAdapter() {
        specializationAdapter = SpecializationSelectionAdapter(
            onItemClick = { industryUI, position ->
                viewModel.updateSelectedIndustry(industryUI)
                specializationAdapter?.updateSelectedItemPosition(position)
                hideKeyBoard()
                viewBinding.specializationEditText.clearFocus()
            },
            onSelectionChanged = { isSelected ->
                updateChooseButtonVisibility(isSelected)
            }
        )
        viewBinding.specializationRecyclerView.adapter = specializationAdapter
        viewBinding.specializationRecyclerView.itemAnimator = null

        viewBinding.specializationRecyclerView.setOnTouchListener { _, _ ->
            hideKeyBoard()
            viewBinding.specializationEditText.clearFocus()
            false
        }
    }

    private fun initSearchDebounce() {
        onSearchDebounce = debounce(
            SEARCH_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            true
        ) {
            viewModel.search(it)
        }
    }

    private fun initListeners() {
        setupToolbar()
        onTextChanged()
    }

    private fun initChooseButtonListener() {
        viewBinding.chooseButton.setOnClickListener {
            viewModel.acceptChanges()
            goBack()
        }
    }

    private fun setupToolbar() {
        viewBinding.toolbar.setOnClickListener {
            goBack()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onTextChanged() {
        with(viewBinding) {
            specializationEditText.doOnTextChanged { text, _, _, _ ->
                val querySearch = text.toString().trim()
                val isEditTextNotEmpty = querySearch.isNotBlank()
                switchSearchIcon(isEditTextNotEmpty)
                onSearchDebounce?.invoke(querySearch)
            }

            specializationEditText.setOnTouchListener { _, event ->
                var handled = false

                if (event.action == MotionEvent.ACTION_UP) {
                    val drawableEnd = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.close_24px
                    )

                    val drawableWidth = drawableEnd?.intrinsicWidth ?: 0
                    val touchAreaStart = specializationEditText.width -
                        specializationEditText.paddingEnd -
                        drawableWidth

                    if (event.rawX >= touchAreaStart) {
                        specializationEditText.text?.clear()
                        hideKeyBoard()
                        handled = true
                    }
                }
                handled
            }
        }
    }

    private fun switchSearchIcon(isEditTextNotEmpty: Boolean) {
        with(viewBinding) {
            val editTextIcon = if (isEditTextNotEmpty) {
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.close_24px
                )
            } else {
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.search_24px
                )
            }
            specializationEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, editTextIcon, null)
        }
    }

    private fun updateChooseButtonVisibility(isVisible: Boolean) {
        viewBinding.chooseButton.isVisible = isVisible
    }

    private fun hideKeyBoard() {
        val inputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val view = activity?.currentFocus ?: view
        view?.let {
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun goBack() {
        parentFragmentManager.popBackStack()
    }

    private fun renderState(state: IndustriesState) {
        with(viewBinding) {
            specializationRecyclerView.isVisible = false
            progressBar.isVisible = false
            errorsTextView.isVisible = false
            errorsImageView.isVisible = false

            when (state) {
                is IndustriesState.Content -> {
                    specializationRecyclerView.isVisible = true
                    specializationAdapter?.updateItems(state.industries, viewModel.savedSelectedIndustry.value)
                    updateChooseButtonVisibility(viewModel.savedSelectedIndustry.value != null)
                }

                is IndustriesState.Loading -> {
                    progressBar.isVisible = true
                }

                is IndustriesState.Error -> {
                    errorsTextView.isVisible = true
                    errorsImageView.isVisible = true
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
