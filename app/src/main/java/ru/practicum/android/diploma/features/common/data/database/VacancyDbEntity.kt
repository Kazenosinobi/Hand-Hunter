package ru.practicum.android.diploma.features.common.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites_table")
data class VacancyDbEntity(
    @PrimaryKey
    @ColumnInfo(name = "vacancy_id")
    val vacancyId: String,
    val title: String,
    @ColumnInfo(name = "salary_from")
    val salaryFrom: Int?,
    @ColumnInfo(name = "salary_to")
    val salaryTo: Int?,
    @ColumnInfo(name = "currency_abbr")
    val currencyAbbr: String?,
    val isGross: Boolean?,
    @ColumnInfo(name = "employer_name")
    val employerName: String,
    val employerLogoUrl: String?,
    val location: String,
    val experience: String,
    @ColumnInfo(name = "employment_form")
    val employmentForm: String,
    val description: String,
    @ColumnInfo(name = "vacancy_url")
    val vacancyUrl: String
)
