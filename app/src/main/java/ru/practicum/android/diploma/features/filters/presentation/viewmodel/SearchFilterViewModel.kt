package ru.practicum.android.diploma.features.filters.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.features.filters.domain.model.Filter

class SearchFilterViewModel : ViewModel() {

    private val _currentFilter = MutableLiveData<Filter>(Filter())
    val currentFilter: LiveData<Filter> = _currentFilter

    val latestSearchFilter = Filter()

    fun getIndustries() {
        // Метод возвращающий "Отрасль"
    }

    // Метод заменяющий отрасль
//    fun setIndustry(industry: IndustryUI?) {
        // TODO
//    }

    // Метод для фильтрации "не показывать без зарплаты"
//    fun setOnlyWithSalary(onlyWithSalary: Boolean) {
        // TODO
//    }

    // Метод заменяющий страну
//    fun setChosenCountry(country: CountryUI?) {
        // TODO
//    }

    // Метод заменяющий регион
//    fun setChosenRegion(region: RegionUI?) {
        // TODO
//    }

    fun setSalary(salary: Int?) {
        // Метод заменяющий сумму ожидаемой зарплаты
    }

    fun clearPlaceOfWork() {
        // Метод очищающий граф "Место работы"
    }

    fun saveFilter() {
        // Метод сохраняющий состояние "фильтра"
    }

    fun retrySearchQueryWithFilterSearch() {
        // Метод повторяющий поисковый запрос
    }

    fun clearFilter() {
        // Метод сбрасывающий настройки фильтра
    }
}
