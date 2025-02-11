package ru.practicum.android.diploma.features.filters.presentation.model.ui

import ru.practicum.android.diploma.features.filters.presentation.model.getPlaceOfWork

/**
 * @param country `Nullable` Хранит экземпляр класса `CountryUI` с информацией о стране
 * @param region `Nullable` Хранит экземпляр класса `RegionUI` с информацией о регионе
 * @param industry `Nullable` Хранит экземпляр класса `IndustryUI` с информацией о регионе
 * @param salary `Nullable` Хранит информацию об уровне зарплаты
 * @param onlyWithSalary  Хранит флаг, указывающий на необходимость игнорирования вакансий без указания зарплаты
 * @property isDefault  Хранит флаг, указывающий на то, являются ли значения фильтра значениями по умолчанию
 */
@Suppress("DataClassShouldBeImmutable")
data class FilterUI(
    val country: String? = null,
    val region: String? = null,
    val industry: String? = null,
    val salary: String? = null,
    val onlyWithSalary: Boolean = false
) {
    val placeOfWork: String? = getPlaceOfWork(country, region)

    val isDefault: Boolean
        get() = country == null && region == null && industry == null && !onlyWithSalary && salary == null
}
