package ru.practicum.android.diploma.features.vacancy.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.util.TypedValueCompat.dpToPx
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyInfoBinding
import ru.practicum.android.diploma.features.common.presentation.ui.BaseFragment
import ru.practicum.android.diploma.features.vacancy.presentation.viewmodel.VacancyInfoViewModel
import ru.practicum.android.diploma.utils.collectWithLifecycle

class VacancyInfoFragment : BaseFragment<FragmentVacancyInfoBinding>() {
    private val wasOpenedFromSearch by lazy {
        arguments?.getBoolean(WAS_OPENED_FROM_SEARCH)
    }

    private val vacancyId by lazy {
        arguments?.getString(VACANCY_ID)
    }

    private val viewModel by viewModel<VacancyInfoViewModel> {
        parametersOf(wasOpenedFromSearch, vacancyId)
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentVacancyInfoBinding {
        return FragmentVacancyInfoBinding.inflate(layoutInflater)
    }

    override fun initUi() {
        with(viewBinding) {
            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            toolbar.setOnMenuItemClickListener {
                handleMenuItemClick(it)
                true
            }

            viewModel.getVacancyInfo()
        }
    }

    override fun observeData() {
        viewModel.state.collectWithLifecycle(this) { state ->
            applyState(state)
        }
    }

    private fun handleMenuItemClick(item: MenuItem) {
        when (item.itemId) {
            R.id.share -> {
                shareVacancy()
            }

            R.id.favourite -> {
                toggleFavouriteIcon(item)
            }

            else -> Unit
        }
    }

    private fun shareVacancy() {
        val vacancyInfo = viewModel.getVacancySharingText()
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, vacancyInfo)
        shareIntent.type = SEND_INTENT_TYPE

        val chooserIntent = Intent.createChooser(shareIntent, null)
        startActivity(chooserIntent)
    }

    private fun toggleFavouriteIcon(item: MenuItem) {
        val isFavourite = viewModel.toggleFavouriteVacancy()
        val iconRes = if (isFavourite) R.drawable.favorites_on_24px else R.drawable.favorites_off_24px
        item.icon = ContextCompat.getDrawable(requireContext(), iconRes)
    }

    private fun applyState(state: VacancyInfoViewModel.State) {
        with(viewBinding) {
            vacancyInfoContainer.isGone = true
            errorContainer.isGone = true
            progressBar.isGone = true

            when (state) {
                is VacancyInfoViewModel.State.Loading -> {
                    progressBar.isVisible = true
                }

                is VacancyInfoViewModel.State.Data -> {
                    setData(state)
                    vacancyInfoContainer.isVisible = true
                }

                is VacancyInfoViewModel.State.ServerError -> {
                    setErrorResource(R.drawable.server_error2, R.string.vacancy_info_server_error)
                    errorContainer.isVisible = true
                }

                is VacancyInfoViewModel.State.NoData -> {
                    setErrorResource(R.drawable.data_delete, R.string.vacancy_info_no_data_error)
                    errorContainer.isVisible = true
                }
            }
        }
    }

    private fun setErrorResource(@DrawableRes drawable: Int, @StringRes string: Int) {
        viewBinding.errorImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, drawable, null))
        viewBinding.errorTextView.text = resources.getText(string)
    }

    private fun setData(state: VacancyInfoViewModel.State.Data) {
        with(viewBinding) {
            vacancyNameTextView.text = state.vacancyInfo.title
            vacancySalaryTextView.text = state.vacancyInfo.salary

            Glide.with(employerInfoView.employerLogoImageView)
                .load(state.vacancyInfo.employerLogoUrl)
                .placeholder(R.drawable.placeholder_32px)
                .transform(
                    FitCenter(),
                    RoundedCorners(
                        dpToPx(
                            resources.getDimension(R.dimen.radius_1x),
                            resources.displayMetrics
                        ).toInt()
                    )
                )
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(employerInfoView.employerLogoImageView)

            employerInfoView.employerNameTextView.text = state.vacancyInfo.employerName
            employerInfoView.vacancyLocationTextView.text = state.vacancyInfo.location
            experienceTextView.text = state.vacancyInfo.experience
            employmentFormTextView.text = state.vacancyInfo.employmentForm
            descriptionTextView.text = Html.fromHtml(
                state.vacancyInfo.description,
                Html.FROM_HTML_MODE_LEGACY or
                    Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM

            ).toString().trim()
            if (state.vacancyInfo.keySkills.isNotEmpty()) {
                keySkillsTitleTextView.isVisible = true
                keySkillsTextView.text = state.vacancyInfo.keySkills
            } else {
                keySkillsTitleTextView.isGone = true
            }

            val menu = toolbar.menu.findItem(R.id.favourite)
            if (state.vacancyInfo.isFavourite) {
                menu.icon = ContextCompat.getDrawable(requireContext(), R.drawable.favorites_on_24px)
            } else {
                menu.icon = ContextCompat.getDrawable(requireContext(), R.drawable.favorites_off_24px)
            }
        }
    }

    companion object {
        private const val SEND_INTENT_TYPE = "text/plain"
        private const val VACANCY_ID = "vacancyId"
        private const val WAS_OPENED_FROM_SEARCH = "wasOpenedFromSearch"

        fun createArgs(wasOpenedFromSearch: Boolean, vacancyId: String): Bundle {
            return bundleOf(
                WAS_OPENED_FROM_SEARCH to wasOpenedFromSearch,
                VACANCY_ID to vacancyId
            )
        }
    }
}
