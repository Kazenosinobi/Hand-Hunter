package ru.practicum.android.diploma.features.common.domain.model

data class VacancyDetails(
    val id: String,
    val title: String,
    val salary: Salary = Salary.stub,
    val employer: Employer = Employer.stub,
    val location: String = "",
    val experience: String = "",
    val employmentType: String = "",
    val description: String = "",
    val keySkills: List<String> = emptyList(),
    val vacancyUrl: String,
    val isFavourite: Boolean = false
)

data class Salary(
    val from: Int = 0,
    val to: Int = 0,
    val currency: CurrencyEnum = CurrencyEnum.RUR,
    val isGross: Boolean? = null
) {
    enum class CurrencyEnum(val symbol: String) {
        AZN("₼"),
        BYR("Br"),
        EUR("€"),
        GEL("₾"),
        KGS("с"),
        KZT("₸"),
        RUR("₽"),
        USD("$"),
        UZS("Soʻm")
    }

    companion object {
        val stub = Salary()
    }
}

data class Employer(
    val name: String = "",
    val logoUrl: String? = null
) {
    companion object {
        val stub = Employer()
    }
}
