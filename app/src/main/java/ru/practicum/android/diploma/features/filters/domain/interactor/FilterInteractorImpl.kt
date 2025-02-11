package ru.practicum.android.diploma.features.filters.domain.interactor

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.features.filters.domain.api.filter.FilterInteractor
import ru.practicum.android.diploma.features.filters.domain.manager.FilterManager
import ru.practicum.android.diploma.features.filters.domain.model.FilterMainData

class FilterInteractorImpl(
    private val filterManager: FilterManager,
) : FilterInteractor {
    override fun clearManager() {
        filterManager.clearManager()
    }

    override fun getDataFromPrefs(): FilterMainData {
        return filterManager.getDataFromPrefs()
    }

    override fun subscribeData(): Flow<FilterMainData> {
        return filterManager.subscribeData()
    }

    override fun keepSalary(salary: String) {
        filterManager.keepSalary(salary)
    }

    override fun keepWithoutSalaryFlag(check: Boolean) {
        filterManager.keepOnlyWithSalaryFlag(check)
    }

    override fun deleteWorkplace() {
        filterManager.deleteWorkplace()
    }

    override fun deleteIndustry() {
        filterManager.deleteIndustry()
    }

    override fun deleteSalary() {
        filterManager.deleteSalary()
    }

    override fun saveData() {
        filterManager.saveData()
    }

    override fun resetData() {
        filterManager.resetData()
    }
}
