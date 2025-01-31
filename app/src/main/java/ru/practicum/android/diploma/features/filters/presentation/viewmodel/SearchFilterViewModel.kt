package ru.practicum.android.diploma.features.filters.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.features.filters.presentation.model.Filter

class SearchFilterViewModel : ViewModel() {

    private val _currentFilter = MutableStateFlow<Filter>(Filter())
    val currentFilter: StateFlow<Filter> = _currentFilter

    val latestSearchFilter = Filter()
    private var oldSalary: Int? = null

    fun getIndustries() {
        // Метод возвращающий "Отрасль"
    }

    // Метод заменяющий отрасль
//    fun setIndustry(industry: IndustryUI?) {
//
//    }

    // Метод для фильтрации "не показывать без зарплаты"
//    fun setOnlyWithSalary(onlyWithSalary: Boolean) {
//
//    }

    // Метод заменяющий страну
//    fun setChosenCountry(country: CountryUI?) {
//
//    }

    // Метод заменяющий регион
//    fun setChosenRegion(region: RegionUI?) {
//
//    }

    fun setSalary(salary: Int?) {
        // Метод заменяющий сумму ожидаемой зарплаты
    }

    // Метод очищающий граф "Место работы"
//    fun clearPlaceOfWork() {
//
//    }

    fun saveFilter() {
        // Метод сохраняющий состояние "фильтра"
    }

    fun retrySearchQueryWithFilterSearch() {
        // Метод повторяющий поисковый запрос
    }

    fun clearFilter() {
        // Метод сбрасывающий настройки фильтра
    }

    fun salaryEnterTextChanged(text: CharSequence?, view: TextInputLayout ) {
        val newSalary = text.toString().toIntOrNull()
        if (oldSalary != newSalary) {
            oldSalary = newSalary
            setSalary(newSalary)
        }
        if (text?.isBlank() == false) {
            view.endIconMode = END_ICON_CLEAR_TEXT
            view.setEndIconDrawable(R.drawable.close_24px)
        } else {
            view.endIconMode = END_ICON_NONE
            view.endIconDrawable = null
        }
    }
}
