package ru.practicum.android.diploma.features.filters.presentation.ui.searchfilter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchFiltersBinding
import ru.practicum.android.diploma.features.common.presentation.ui.BaseFragment
import ru.practicum.android.diploma.features.filters.presentation.model.ui.FilterUI

class SearchFiltersFragment : BaseFragment<FragmentSearchFiltersBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchFiltersBinding {
        return FragmentSearchFiltersBinding.inflate(inflater, container, false)
    }

    private val viewModel: SearchFilterViewModel by viewModel<SearchFilterViewModel>()

    override fun onStart() {
        super.onStart()
        viewModel.getData()
    }

    override fun initUi() {
        initializeViews()
    }

    override fun observeData() {
        viewModel.stateFlowFilterUI
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .distinctUntilChanged()
            .onEach { filterUI ->
                processFilterResult(filterUI)
                setupClearButton(
                    filterUI?.country,
                    viewBinding.placeOfWorkContainer
                ) { viewModel.deletePlaceOfWork() }
                setupClearButton(
                    filterUI?.industry,
                    viewBinding.industryContainer
                ) { viewModel.deleteIndustry() }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeViews() {
        with(viewBinding) {
            placeOfWorkEditText.setOnClickListener {
                findNavController().navigate(R.id.action_searchFiltersFragment_to_workplaceSelectionFragment)
            }

            industryEditText.setOnClickListener {
                findNavController().navigate(R.id.action_searchFiltersFragment_to_specializationSelectionFragment)
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            resetButton.setOnClickListener {
                viewModel.clearFilter()
            }

            acceptButton.setOnClickListener {
                findNavController().navigateUp()
            }

            salaryEditText.doOnTextChanged { s, _, _, _ ->
                viewModel.salaryEnterTextChanged(s)
                salaryEnterClearIcon(s)
            }

            withoutSalary.setOnClickListener {
                viewModel.setOnlyWithSalary(withoutSalary.isChecked)
            }

            @Suppress("LabeledExpression")
            salaryEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    salaryEditText.clearFocus()
                    hideKeyBoard()
                    return@setOnEditorActionListener true
                } else {
                    return@setOnEditorActionListener false
                }
            }

            viewBinding.root.setOnTouchListener { _, _ ->
                hideKeyBoard()
                salaryEditText.clearFocus()
                false
            }
        }
    }

    private fun hideKeyBoard() {
        val inputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val view = activity?.currentFocus ?: view
        view?.let {
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun salaryEnterClearIcon(text: CharSequence?) {
        with(viewBinding) {
            if (text?.isBlank() == false) {
                imageClear.isVisible = true
                imageClear.setOnClickListener {
                    viewModel.deleteSalary()
                    salaryEditText.text?.clear()
                    salaryEditText.clearFocus()
                    hideKeyBoard()
                }
            } else {
                salaryFrameContainer.endIconMode = END_ICON_NONE
                imageClear.isVisible = false
            }
        }
    }

    private fun <T> setupClearButton(item: T?, til: TextInputLayout, action: () -> Unit) {
        if (item != null) {
            til.setEndIconDrawable(R.drawable.close_24px)
            til.setEndIconOnClickListener {
                action.invoke()
                til.setEndIconOnClickListener(null)
            }
        } else {
            til.setEndIconDrawable(R.drawable.arrow_forward_24px)
            til.isEndIconVisible = false
            til.isEndIconVisible = true
        }
    }

    private fun processFilterResult(filter: FilterUI?) {
        with(viewBinding) {
            setButtonVisibility(filter)
            if (filter?.isDefault == false) {
                processArea(filter.country, filter.region)
                industryEditText.setText(filter.industry ?: "")
                withoutSalary.isChecked = filter.onlyWithSalary
                val newSalary = filter.salary
                if (newSalary != viewModel.oldSalary) {
                    salaryEditText.setText(newSalary)
                }
            } else {
                placeOfWorkEditText.text = null
                industryEditText.text = null
                withoutSalary.isChecked = false
                salaryEditText.text = null
            }
        }
        setCheckedIcon(filter?.onlyWithSalary ?: false)
    }

    private fun processArea(country: String?, region: String?) {
        var result = ""
        if (country != null) result += country
        if (region != null) result += ", $region"
        viewBinding.placeOfWorkEditText.setText(result)
    }

    private fun setButtonVisibility(filterUI: FilterUI?) {
        val isVisible = filterUI != FilterUI()
        with(viewBinding) {
            resetButton.isVisible = isVisible
            acceptButton.isVisible = isVisible
        }
    }

    private fun setCheckedIcon(isChecked: Boolean) {
        with(viewBinding) {
            if (isChecked) {
                withoutSalary.icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.check_box_on_24px)
            } else {
                withoutSalary.icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.check_box_off_24px)
                viewModel.deleteShowWithoutSalary()
            }
        }
    }
}
