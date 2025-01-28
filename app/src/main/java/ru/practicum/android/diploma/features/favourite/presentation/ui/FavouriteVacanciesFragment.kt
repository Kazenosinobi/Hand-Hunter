package ru.practicum.android.diploma.features.favourite.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFavouriteVacanciesBinding
import ru.practicum.android.diploma.features.common.presentation.ui.BaseFragment
import ru.practicum.android.diploma.features.favourite.presentation.model.FavouriteVacanciesState
import ru.practicum.android.diploma.features.favourite.presentation.viewmodel.FavouriteVacanciesViewModel
import ru.practicum.android.diploma.features.common.presentation.models.VacancySearchUI
import ru.practicum.android.diploma.features.common.presentation.recycler.VacancyAdapter
import ru.practicum.android.diploma.features.vacancy.presentation.ui.VacancyInfoFragment
import ru.practicum.android.diploma.utils.collectWithLifecycle
import ru.practicum.android.diploma.utils.debounce

class FavouriteVacanciesFragment : BaseFragment<FragmentFavouriteVacanciesBinding>() {

    private val viewModel: FavouriteVacanciesViewModel by viewModel<FavouriteVacanciesViewModel>()

    private var vacancyAdapter: VacancyAdapter? = null

    private var onVacancyClickDebounce: ((VacancySearchUI) -> Unit?)? = null

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFavouriteVacanciesBinding {
        return FragmentFavouriteVacanciesBinding.inflate(inflater, container, false)
    }

    override fun initUi() {
        initAdapter()
        initClickDebounce()
    }

    override fun observeData() {
        viewModel.state.collectWithLifecycle(this) {
            applyState(it)
        }
    }

    private fun initAdapter() {
        vacancyAdapter = VacancyAdapter(
            onClick = { vacancy ->
                onVacancyClickDebounce?.let { it(vacancy) }
            }
        )

        with(viewBinding) {
            favouriteRecyclerView.adapter = vacancyAdapter
            favouriteRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            favouriteRecyclerView.itemAnimator = null
        }

        vacancyAdapter?.submitList(emptyList())
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
                R.id.action_favouriteVacanciesFragment_to_vacancyInfoFragment,
                VacancyInfoFragment.createArgs(false, vacancyId)
            )
    }

    private fun applyState(state: FavouriteVacanciesState) {
        with(viewBinding) {
            progressBar.isGone = true
            groupOfErrorContainer.isGone = true
            favouriteRecyclerView.isGone = true
        }

        when (state) {
            is FavouriteVacanciesState.Content -> showContent(state.vacancies)
            is FavouriteVacanciesState.Empty -> showEmpty()
            is FavouriteVacanciesState.Loading -> showLoading()
            is FavouriteVacanciesState.Error -> showError()
        }
    }

    private fun showContent(vacancies: List<VacancySearchUI>) {
        vacancyAdapter?.submitList(vacancies)
        viewBinding.favouriteRecyclerView.isVisible = true
    }

    private fun showLoading() {
        viewBinding.progressBar.isVisible = true
    }

    private fun showEmpty() {
        with(viewBinding) {
            groupOfErrorContainer.isVisible = true
            messageErrorTextView.setText(R.string.message_error_empty_data)
            errorImageView.setImageResource(R.drawable.empty_list)
        }
    }

    private fun showError() {
        with(viewBinding) {
            groupOfErrorContainer.isVisible = true
            messageErrorTextView.setText(R.string.message_error_could_not_get_data)
            errorImageView.setImageResource(R.drawable.bad_search)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vacancyAdapter = null
    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}
