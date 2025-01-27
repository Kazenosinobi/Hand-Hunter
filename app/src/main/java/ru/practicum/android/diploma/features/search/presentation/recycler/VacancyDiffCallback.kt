package ru.practicum.android.diploma.features.search.presentation.recycler

import androidx.recyclerview.widget.DiffUtil
import ru.practicum.android.diploma.features.common.presentation.models.VacancySearchUI

class VacancyDiffCallback : DiffUtil.ItemCallback<VacancySearchUI>() {
    override fun areItemsTheSame(oldItem: VacancySearchUI, newItem: VacancySearchUI): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: VacancySearchUI, newItem: VacancySearchUI): Boolean {
        return oldItem == newItem
    }
}
