package ru.practicum.android.diploma.features.filters.presentation.ui.searchfilter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.features.filters.domain.api.filter.FilterInteractor
import ru.practicum.android.diploma.features.filters.domain.model.FilterMainData
import ru.practicum.android.diploma.features.filters.presentation.model.state.SearchFilterState
import ru.practicum.android.diploma.features.filters.presentation.model.toUI
import ru.practicum.android.diploma.features.filters.presentation.model.ui.FilterUI

class SearchFilterViewModel(
    private val filterInteractor: FilterInteractor
) : ViewModel() {

    private val _stateFlowFilterUI = MutableStateFlow(SearchFilterState.Content(FilterUI()))
    val stateFlowFilterUI = _stateFlowFilterUI.asStateFlow()

    private var currentSearchFilter: FilterMainData = filterInteractor.getDataFromPrefs()
    var oldSalary: String? = null
        private set

    init {
        filterInteractor.clearManager()
    }

    fun subscribeData() {
        viewModelScope.launch {
            filterInteractor.subscribeData().collect { data ->
                val newData = data.toUI()
                _stateFlowFilterUI.value = SearchFilterState.Content(newData, isVisibleAcceptButton(newData))
            }
        }
    }

    fun setOnlyWithSalary(onlyWithSalary: Boolean) {
        filterInteractor.keepWithoutSalaryFlag(onlyWithSalary)
    }

    private fun setSalary(salary: String?) {
        if (!salary.isNullOrBlank()) {
            filterInteractor.keepSalary(salary)
        }
    }

    fun deletePlaceOfWork() {
        filterInteractor.deleteWorkplace()
    }

    fun deleteIndustry() {
        filterInteractor.deleteIndustry()
    }

    fun deleteSalary() {
        filterInteractor.deleteSalary()
    }

    fun acceptChanges() {
        filterInteractor.saveData()
    }

    fun resetFilter() {
        filterInteractor.resetData()
        currentSearchFilter = filterInteractor.getDataFromPrefs()
    }

    fun salaryEnterTextChanged(text: CharSequence?) {
        if (text == null) return
        val newSalary = text.toString().trim()
        if (oldSalary != newSalary) {
            oldSalary = newSalary
            setSalary(newSalary)
        }
    }

    private fun isVisibleAcceptButton(data: FilterUI): Boolean {
        return currentSearchFilter.toUI() != data
    }
}
