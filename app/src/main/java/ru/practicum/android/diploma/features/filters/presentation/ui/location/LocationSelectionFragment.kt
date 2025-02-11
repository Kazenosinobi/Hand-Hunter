package ru.practicum.android.diploma.features.filters.presentation.ui.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentLocationSelectionBinding
import ru.practicum.android.diploma.features.common.presentation.ui.BaseFragment
import ru.practicum.android.diploma.features.filters.presentation.model.api.Regionable
import ru.practicum.android.diploma.features.filters.presentation.model.state.LocationSelectionState
import ru.practicum.android.diploma.features.filters.presentation.ui.location.recycler.LocationAdapter
import ru.practicum.android.diploma.utils.collectWithLifecycle
import ru.practicum.android.diploma.utils.debounce

class LocationSelectionFragment : BaseFragment<FragmentLocationSelectionBinding>() {

    private val isCountry by lazy {
        arguments?.getBoolean(IS_COUNTRY)
    }

    private val countryId by lazy {
        arguments?.getString(COUNTRY_ID)
    }

    private val viewModel: LocationSelectionViewModel by viewModel<LocationSelectionViewModel> {
        parametersOf(isCountry, countryId)
    }

    private var locationAdapter: LocationAdapter? = null

    private var onLocationClickDebounce: ((Regionable) -> Unit?)? = null
    private var onSearchDebounce: ((String) -> Unit)? = null

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLocationSelectionBinding {
        return FragmentLocationSelectionBinding.inflate(inflater, container, false)
    }

    override fun initUi() {
        initAdapter()
        initClickDebounce()
        initSearchDebounce()
        initListeners()
    }

    override fun observeData() {
        viewModel.state.collectWithLifecycle(this) {
            applyState(it)
        }
    }

    private fun applyState(state: LocationSelectionState) {
        with(viewBinding) {
            progressBar.isGone = true
            contentRecyclerView.isGone = true
            errorContainer.isGone = true
            searchEditText.isGone = true
        }

        when (state) {
            is LocationSelectionState.ContentCountry -> showContent(true, state.countries)
            is LocationSelectionState.ContentRegion -> showContent(false, state.regions)
            is LocationSelectionState.Loading -> isCountry?.let { showLoading(it) }
            is LocationSelectionState.NetworkError -> showNetworkError()
            is LocationSelectionState.NoRegionError -> showNoRegionError()
        }
    }

    private fun setTittleText(isCountry: Boolean): String {
        return getString(
            if (isCountry) {
                R.string.location_country_toolbar_title
            } else {
                R.string.location_region_toolbar_title
            }
        )
    }

    private fun showContent(isCountry: Boolean, list: List<Regionable>) {
        with(viewBinding) {
            locationAdapter?.submitList(list)
            contentRecyclerView.isVisible = true
            toolbar.title = setTittleText(isCountry)
            searchEditText.isVisible = isCountry.not()
        }
    }

    private fun showLoading(isCountry: Boolean) {
        viewBinding.progressBar.isVisible = true
        viewBinding.toolbar.title = setTittleText(isCountry)
    }

    private fun showNetworkError() {
        with(viewBinding) {
            errorContainer.isVisible = true
            viewBinding.searchEditText.isVisible = isCountry?.not() == true
            messageErrorTextView.setText(R.string.location_server_error)
            errorImageView.setImageResource(R.drawable.something_went_wrong)
        }
    }

    private fun showNoRegionError() {
        with(viewBinding) {
            viewBinding.searchEditText.isVisible = true
            errorContainer.isVisible = true
            messageErrorTextView.setText(R.string.location_no_region_error)
            errorImageView.setImageResource(R.drawable.bad_search)
        }
    }

    @Suppress("EmptyFunctionBlock")
    private fun initAdapter() {
        locationAdapter = LocationAdapter(
            onClick = { region ->
                onLocationClickDebounce?.let { it(region) }
            }
        )

        with(viewBinding) {
            contentRecyclerView.adapter = locationAdapter
            contentRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            contentRecyclerView.itemAnimator = null
        }

        locationAdapter?.submitList(emptyList())
        locationAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(
                positionStart: Int,
                itemCount: Int
            ) {
                viewBinding.contentRecyclerView.scrollToPosition(0)
            }

            override fun onItemRangeRemoved(
                positionStart: Int,
                itemCount: Int
            ) {
            }
        })
    }

    private fun initClickDebounce() {
        onLocationClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { region ->
            goBack(region)
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
        viewBinding.toolbar.setOnClickListener {
            goBack(null)
        }

        onTextChanged()
        clearSearchString()
    }

    private fun goBack(region: Regionable?) {
        region?.let {
            viewModel.saveRegion(region)
        }
        parentFragmentManager.popBackStack()
    }

    private fun onTextChanged() {
        with(viewBinding) {
            searchEditText.doOnTextChanged { text, _, _, _ ->
                val querySearch = text.toString()
                val isEditTextNotEmpty = text.isNullOrEmpty().not()
                switchSearchClearIcon(isEditTextNotEmpty)
                onSearchDebounce?.invoke(querySearch)
            }
        }
    }

    private fun switchSearchClearIcon(isEditTextNotEmpty: Boolean) {
        with(viewBinding) {
            val icon = ContextCompat.getDrawable(
                requireContext(),
                if (isEditTextNotEmpty) {
                    R.drawable.close_24px
                } else {
                    R.drawable.search_24px
                }
            )
            searchEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
        }
    }

    @Suppress("LabeledExpression")
    @SuppressLint("ClickableViewAccessibility")
    private fun clearSearchString() {
        viewBinding.searchEditText.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = viewBinding.searchEditText.compoundDrawables[2] // DrawableEnd (Right)
                if (drawableEnd != null) {
                    val drawableStartX =
                        viewBinding.searchEditText.width -
                            viewBinding.searchEditText.paddingEnd -
                            drawableEnd.intrinsicWidth
                    if (event.rawX >= drawableStartX) {
                        viewBinding.searchEditText.text.clear()
                        hideKeyBoard()
                        view.performClick()
                        return@setOnTouchListener true
                    }
                }
            }
            return@setOnTouchListener false
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

    override fun onDestroyView() {
        super.onDestroyView()
        locationAdapter = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 100L
        private const val SEARCH_DEBOUNCE_DELAY = 500L
        private const val IS_COUNTRY = "is_country"
        private const val COUNTRY_ID = "country_id"

        fun createArgs(isCountry: Boolean): Bundle {
            return androidx.core.os.bundleOf(
                IS_COUNTRY to isCountry
            )
        }
    }
}
