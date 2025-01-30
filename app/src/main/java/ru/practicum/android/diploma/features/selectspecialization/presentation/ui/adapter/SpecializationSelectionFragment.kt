package ru.practicum.android.diploma.features.selectspecialization.presentation.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSpecializationSelectionBinding
import ru.practicum.android.diploma.features.common.presentation.ui.BaseFragment
import ru.practicum.android.diploma.features.selectspecialization.presentation.model.IndustriesState
import ru.practicum.android.diploma.features.selectspecialization.presentation.model.IndustryUI
import ru.practicum.android.diploma.features.selectspecialization.presentation.viewmodel.SpecializationSelectionViewModel
import ru.practicum.android.diploma.utils.collectWithLifecycle
import ru.practicum.android.diploma.utils.debounce

class SpecializationSelectionFragment : BaseFragment<FragmentSpecializationSelectionBinding>() {

    private var _binding: FragmentSpecializationSelectionBinding? = null
    val binding get() = _binding!!

    private var specializationAdapter: SpecializationSelectionAdapter? = null
    private var onIndustryClickDebounce: ((IndustryUI) -> Unit?)? = null
    private var onSearchDebounce: ((String) -> Unit)? = null

    private val viewModel by viewModel<SpecializationSelectionViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSpecializationSelectionBinding {
        return FragmentSpecializationSelectionBinding.inflate(inflater, container, false)
    }

    override fun initUi() {
        initAdapter()
        initSearchDebounce()
        initListeners()
    }

    override fun observeData() {
        viewModel.getIndustriesState().collectWithLifecycle(this) {
            renderState(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        specializationAdapter = null
        _binding = null
    }

    private fun initAdapter() {
        specializationAdapter = SpecializationSelectionAdapter(onItemClick = { industry ->
            binding.specializationEditText.setText(industry.name)

            specializationAdapter?.updateItems(listOf(industry))

            binding.chooseButton.isVisible = true
            hideKeyBoard()
        })
        binding.specializationRecyclerView.adapter = specializationAdapter
    }

    private fun initSearchDebounce() {
        onSearchDebounce = debounce(
            SEARCH_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            true
        ) { querySearch ->
            viewModel.getSpecialization(querySearch)
        }
    }

    private fun initListeners() {
        setupToolbar()
        onTextChanged()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnClickListener {
            goBack()
        }
    }

    private fun onTextChanged() {
        with(binding) {
            specializationEditText.doOnTextChanged { text, _, _, _ ->
                val querySearch = text.toString()
                val isEditTextNotEmpty = text.isNullOrEmpty().not()
                switchSearchIcon(isEditTextNotEmpty)
                onSearchDebounce?.invoke(querySearch)
            }
        }
    }

    private fun switchSearchIcon(isEditTextNotEmpty: Boolean) {
        with(binding) {
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
        with(binding) {
            when (state) {
                is IndustriesState.Content -> {
                    specializationRecyclerView.isVisible = true
                    progressBar.isVisible = false
                    errorsTextView.isVisible = false
                    errorsImageView.isVisible = false
                    chooseButton.isVisible = false
                }
                is IndustriesState.Loading -> {
                    specializationRecyclerView.isVisible = false
                    progressBar.isVisible = true
                    errorsTextView.isVisible = false
                    errorsImageView.isVisible = false
                    chooseButton.isVisible = false
                }

                is IndustriesState.Error -> {
                    specializationRecyclerView.isVisible = false
                    progressBar.isVisible = false
                    errorsTextView.isVisible = true
                    errorsImageView.isVisible = true
                    chooseButton.isVisible = false
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}





