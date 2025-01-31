package ru.practicum.android.diploma.features.filters.presentation.model

/**
 * @param country `Nullable` Хранит экземпляр класса `CountryUI` с информацией о стране
 * @param region `Nullable` Хранит экземпляр класса `RegionUI` с информацией о регионе
 * @param industry `Nullable` Хранит экземпляр класса `IndustryUI` с информацией о регионе
 * @param salary `Nullable` Хранит информацию об уровне зарплаты
 * @param onlyWithSalary  Хранит флаг, указывающий на необходимость игнорирования вакансий без указания зарплаты
 * @property isDefault  Хранит флаг, указывающий на то, являются ли значения фильтра значениями по умолчанию
 */

data class FilterUI(
    // val country: CountryUI? = null,
    // val region: RegionUI? = null,
    // val industry: IndustryUI? = null,
    val salary: Int? = null,
    val onlyWithSalary: Boolean = false
) {
//    val isDefault: Boolean
//        get() = country == null && region == null && industry == null && !onlyWithSalary && salary == null
}
