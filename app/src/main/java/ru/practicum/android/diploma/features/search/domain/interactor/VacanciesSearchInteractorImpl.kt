package ru.practicum.android.diploma.features.search.domain.interactor

import ru.practicum.android.diploma.features.filters.domain.api.filter.FilterRepository
import ru.practicum.android.diploma.features.filters.domain.model.FilterMainData
import ru.practicum.android.diploma.features.search.domain.api.VacanciesSearchRepository
import ru.practicum.android.diploma.features.search.domain.model.QuerySearch
import ru.practicum.android.diploma.features.search.domain.model.Vacancies

class VacanciesSearchInteractorImpl(
    private val repository: VacanciesSearchRepository,
    private val filterRepository: FilterRepository
) : VacanciesSearchInteractor {
    override suspend fun getVacancies(querySearch: QuerySearch): Result<Vacancies> {
        return repository.searchVacancies(querySearch).mapCatching { it }
    }

    override fun getFilters(): FilterMainData? {
        val filters = filterRepository.getFilterMainData()
        return if (isContented(filters)) filters else null
    }

    private fun isContented(filters: FilterMainData): Boolean {
        return with(filters) {
            region.id.isNotEmpty() ||
                country.id.isNotEmpty() ||
                industry.id.isNotEmpty() ||
                salary.isNotEmpty() ||
                isNeedToHideVacancyWithoutSalary
        }
    }

}
