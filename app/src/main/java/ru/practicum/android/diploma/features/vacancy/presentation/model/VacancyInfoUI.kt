package ru.practicum.android.diploma.features.vacancy.presentation.model

data class VacancyInfoUI(
    val title: String,
    val salary: String,
    val employerLogoUrl: String,
    val employerName: String,
    val location: String,
    val experience: String,
    val employmentForm: String,
    val description: String,
    val keySkills: String,
    val isFavourite: Boolean
)
