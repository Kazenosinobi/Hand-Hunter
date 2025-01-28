package ru.practicum.android.diploma.features.search.presentation.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.features.common.presentation.ui.BaseFragment
import ru.practicum.android.diploma.features.search.domain.model.QuerySearch
import ru.practicum.android.diploma.features.search.presentation.model.SearchState
import ru.practicum.android.diploma.features.search.presentation.model.VacanciesSearchUI
import ru.practicum.android.diploma.features.common.presentation.models.VacancySearchUI
import ru.practicum.android.diploma.features.common.presentation.recycler.VacancyAdapter
import ru.practicum.android.diploma.features.search.presentation.viewmodel.SearchViewModel
import ru.practicum.android.diploma.features.vacancy.presentation.ui.VacancyInfoFragment
import ru.practicum.android.diploma.utils.debounce

class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private var vacancyAdapter: VacancyAdapter? = null

    private var onVacancyClickDebounce: ((VacancySearchUI) -> Unit?)? = null
    private var onSearchDebounce: ((QuerySearch) -> Unit)? = null

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModel<SearchViewModel>()

    override fun onDestroyView() {
        super.onDestroyView()
        vacancyAdapter = null
    }

    override fun initUi() {
        initSearchDebounce()
        initClickDebounce()
        initAdapters()
        initListeners()
    }

    override fun observeData() {
        viewModel.getSearchStateFlow()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { searchState ->
                with(viewBinding) {
                    progressBar.isVisible = false
                    contentRecyclerView.isVisible = false
                    messageTextView.isVisible = false
                    errorsImageView.isVisible = false
                    errorsTextView.isVisible = false
                }
                when (searchState) {
                    SearchState.Loading -> showProgressBar()
                    is SearchState.Content -> showVacancies(searchState.vacancies)
                    SearchState.Init -> showInit()
                    SearchState.EmptyError -> showEmptyError()
                    SearchState.NetworkError -> showNetworkError()
                    SearchState.ServerError -> showServerError()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun showProgressBar() {
        viewBinding.progressBar.isVisible = true
    }

    private fun showVacancies(vacancies: VacanciesSearchUI) {
        vacancyAdapter?.submitList(vacancies.items)
        with(viewBinding) {
            messageTextView.text = getTotalVacanciesText(vacancies)
            contentRecyclerView.isVisible = true
            messageTextView.isVisible = true
        }
    }

    private fun showEmptyError() {
        with(viewBinding) {
            errorsImageView.setImageResource(R.drawable.bad_search)
            errorsTextView.setText(R.string.bad_request)
            messageTextView.setText(R.string.message_text)
            messageTextView.isVisible = true
            errorsImageView.isVisible = true
            errorsTextView.isVisible = true
        }
    }

    private fun showNetworkError() {
        with(viewBinding) {
            errorsImageView.setImageResource(R.drawable.no_internet)
            errorsTextView.setText(R.string.bad_internet)
            errorsImageView.isVisible = true
            errorsTextView.isVisible = true
        }
    }

    private fun showServerError() {
        with(viewBinding) {
            errorsImageView.setImageResource(R.drawable.server_error)
            errorsTextView.setText(R.string.server_error)
            errorsImageView.isVisible = true
            errorsTextView.isVisible = true
        }
    }

    private fun showInit() {
        vacancyAdapter?.submitList(emptyList())
        with(viewBinding) {
            errorsTextView.text = EMPTY_TEXT
            errorsImageView.setImageResource(R.drawable.empty_search)
            errorsImageView.isVisible = true
            errorsTextView.isVisible = true

            searchEditText.setText(EMPTY_TEXT)
            searchEditText.clearFocus()
        }
        hideKeyBoard()
    }

    private fun getTotalVacanciesText(vacancies: VacanciesSearchUI): String {
        val vacanciesCount = vacancies.found
        return resources.getQuantityString(
            R.plurals.vacancies_count,
            vacanciesCount,
            vacanciesCount
        )
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

    private fun initClickDebounce() {
        onVacancyClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { vacancy ->
            startVacancyInfoFragment(vacancy.id)
        }
    }

    private fun startVacancyInfoFragment(vacancyId: String) {
        findNavController()
            .navigate(
                R.id.action_searchFragment_to_vacancyInfoFragment,
                VacancyInfoFragment.createArgs(true, vacancyId)
            )
    }

    private fun initAdapters() {
        vacancyAdapter = VacancyAdapter(
            onClick = { vacancy ->
                onVacancyClickDebounce?.let { it(vacancy) }
            }
        )

        with(viewBinding) {
            contentRecyclerView.adapter = vacancyAdapter
            contentRecyclerView.itemAnimator = null
        }
    }

    private fun initListeners() {
        setupSearchEditTextTouchListener()
        onTextChanged()
        setupEnterKeyListener()
    }

    private fun setupSearchEditTextTouchListener() {
        with(viewBinding.searchEditText) {
            setOnTouchListener { _, event ->
                if (isRightDrawableClicked(event)) {
                    viewModel.onClearedSearch()
                    performClick()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun onTextChanged() {
        with(viewBinding) {
            searchEditText.doOnTextChanged { text, _, _, _ ->
                val isNotEmpty = text.isNullOrEmpty().not()
                val querySearch = QuerySearch(text = text.toString().trim())
                if (!isNotEmpty) {
                    viewModel.onClearedSearch()
                }

                val drawableEnd = if (isNotEmpty) {
                    ContextCompat.getDrawable(requireContext(), R.drawable.close_24px)
                } else {
                    ContextCompat.getDrawable(requireContext(), R.drawable.search_24px)
                }
                searchEditText.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    drawableEnd,
                    null
                )
                onSearchDebounce?.invoke(querySearch)
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

    private fun setupEnterKeyListener() {
        viewBinding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val queryText = viewBinding.searchEditText.text.toString().trim()
                if (queryText.isNotEmpty()) {
                    val querySearch = QuerySearch(text = queryText)
                    viewModel.search(querySearch)
                    hideKeyBoard()
                }
                true
            } else {
                false
            }
        }
    }

    private fun EditText.isRightDrawableClicked(event: MotionEvent): Boolean {
        val rightDrawable = compoundDrawables[RIGHT_CORNER]
        val drawableWidth = rightDrawable?.bounds?.width()
        if (event.action != MotionEvent.ACTION_UP || drawableWidth == null) return false

        return event.x >= width - paddingEnd - drawableWidth
    }

    private companion object {
        private const val EMPTY_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 100L
        private const val RIGHT_CORNER = 2
    }

}
