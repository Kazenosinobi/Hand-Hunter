package ru.practicum.android.diploma.features.common.data.network.dto.vacancy.details

import com.google.gson.annotations.SerializedName
import ru.practicum.android.diploma.features.common.data.network.dto.area.AreaEntity

/**
 * Сущность для получения подробной информации о вакансии
 * @param id - идентификатор вакансии
 * @param name - название вакансии
 * @param area - область
 * @param employer - информация о работодателе (логотип, название организации)
 * @param salary - информация о заработной плате (вилка или же только верхняя/нижняя граница, если в вакансии не указана з/п - значение null)
 * @param experience - информация о требуемом опыте для отклика на данную вакансию
 * @param employment - информация о типе занятости
 * @param description - описание вакансии
 * @param keySkills - информация о требуемых навыках для отклика на данную вакансию
 * @param vacancyUrl - ссылка на вакансию
 */
data class DetailsVacancyEntity(
    val id: String,
    val name: String,
    val salary: SalaryEntity?,
    val employer: EmployerEntity,
    val address: AddressEntity?,
    val area: AreaEntity,
    val experience: ExperienceEntity,
    @SerializedName("employment")
    val employment: EmploymentEntity,
    val description: String,
    @SerializedName("key_skills")
    val keySkills: List<SkillEntity>,
    @SerializedName("alternate_url")
    val vacancyUrl: String
) {
    data class ExperienceEntity(
        val id: String,
        val name: String
    )

    data class EmploymentEntity(
        val id: String,
        val name: String
    )

    data class SkillEntity(
        val name: String
    )
}

data class AddressEntity(
    val city: String?,
    val street: String?,
    val building: String?
)
