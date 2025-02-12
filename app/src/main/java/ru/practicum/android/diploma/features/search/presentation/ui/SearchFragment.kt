package ru.practicum.android.diploma.features.search.presentation.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.features.common.presentation.models.VacancySearchUI
import ru.practicum.android.diploma.features.common.presentation.recycler.VacancyAdapter
import ru.practicum.android.diploma.features.common.presentation.ui.BaseFragment
import ru.practicum.android.diploma.features.search.presentation.model.SearchState
import ru.practicum.android.diploma.features.search.presentation.model.VacanciesSearchUI
import ru.practicum.android.diploma.features.search.presentation.viewmodel.SearchViewModel
import ru.practicum.android.diploma.features.vacancy.presentation.ui.VacancyInfoFragment
import ru.practicum.android.diploma.utils.debounce

@Suppress("LargeClass")
class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private var vacancyAdapter: VacancyAdapter? = null

    private var onVacancyClickDebounce: ((VacancySearchUI) -> Unit?)? = null
    private var onSearchDebounce: ((String) -> Unit)? = null
    private val viewModel by viewModel<SearchViewModel>()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(layoutInflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vacancyAdapter = null
    }

    override fun initUi() {
        viewModel.getFilters()
        isShouldStartSearch()
        initClickDebounce()
        initAdapters()
        initListeners()
        viewBinding.filter.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_searchFiltersFragment)
        }
    }

    override fun observeData() {
        viewModel.getSearchStateFlow()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { searchState ->
                with(viewBinding) {
                    bottomProgressBar.isVisible = false
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
                    SearchState.Pagination -> showBottomProgressBar()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.getToastEventFlow()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .combine(viewModel.networkErrorStateFlow) { event, hasError ->
                if (hasError) event else null
            }
            .distinctUntilChanged()
            .filterNotNull()
            .onEach { event ->
                when (event) {
                    is SearchViewModel.ToastEvent.NetworkError -> {
                        showToast(getString(R.string.bad_connection))
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.isSearchWithFilters()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .distinctUntilChanged()
            .onEach { hasFilters ->
                switchFilterIcon(hasFilters)
                if (hasFilters) {
                    viewModel.performSearchWithFilters()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showBottomProgressBar() {
        viewBinding.bottomProgressBar.isVisible = true
        viewBinding.contentRecyclerView.isVisible = true
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
            viewModel.performSearch(it)
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

        with(viewBinding) {
            contentRecyclerView.adapter = vacancyAdapter
            contentRecyclerView.itemAnimator = null
            contentRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return

                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!viewModel.isLoading && visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    private fun initListeners() {
        onTextChanged()
        setupEnterKeyListener()
        clearSearchString()
    }

    private fun clearSearchString() {
        with(viewBinding) {
            searchTextInput.setEndIconOnClickListener {
                viewModel.onClearedSearch()
                searchEditText.text?.clear()
                searchEditText.clearFocus()
                hideKeyBoard()
            }
        }
    }

    private fun switchSearchClearIcon(isEditTextEmpty: Boolean) {
        with(viewBinding) {
            val image = if (isEditTextEmpty.not()) {
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
            searchTextInput.endIconDrawable = image
        }
    }

    private fun switchFilterIcon(hasFilters: Boolean) {
        with(viewBinding) {
            val image = if (hasFilters) {
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.filter_on_24px
                )
            } else {
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.filter_off_24px
                )
            }
            filter.setImageDrawable(image)
        }
    }

    private fun onTextChanged() {
        with(viewBinding) {
            searchTextInput.isHintEnabled = false
            searchEditText.doOnTextChanged { text, _, _, _ ->
                if (text.isNullOrEmpty()) {
                    viewModel.onClearedSearch()
                }
                switchSearchClearIcon(text.isNullOrEmpty())
                onSearchDebounce?.invoke(text.toString().trim())
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
                    viewModel.performSearch(queryText)
                    hideKeyBoard()
                }
                true
            } else {
                false
            }
        }
    }

    private fun isShouldStartSearch() {
        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
        val isReturning = savedStateHandle?.get<Boolean>(IS_RETURNING) ?: false

        if (!isReturning) {
            initSearchDebounce()
        } else {
            savedStateHandle?.set(IS_RETURNING, false)
        }
    }

    private companion object {
        private const val EMPTY_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 100L
        private const val IS_RETURNING = "isReturning"
    }
}
