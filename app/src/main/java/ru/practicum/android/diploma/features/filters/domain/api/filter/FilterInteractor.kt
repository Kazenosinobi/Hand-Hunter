package ru.practicum.android.diploma.features.filters.domain.api.filter

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.features.filters.domain.model.FilterMainData

interface FilterInteractor {
    fun subscribeData(): Flow<FilterMainData>
    fun clearManager()

    fun getDataFromPrefs(): FilterMainData

    fun keepSalary(salary: String)
    fun keepWithoutSalaryFlag(check: Boolean)

    fun deleteWorkplace()
    fun deleteIndustry()
    fun deleteSalary()

    fun saveData()
    fun resetData()
}
