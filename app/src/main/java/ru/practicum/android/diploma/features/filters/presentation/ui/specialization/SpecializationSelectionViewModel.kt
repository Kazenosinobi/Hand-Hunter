package ru.practicum.android.diploma.features.filters.presentation.ui.specialization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.features.common.domain.CustomException
import ru.practicum.android.diploma.features.filters.domain.api.specialization.SpecializationInteractor
import ru.practicum.android.diploma.features.filters.presentation.model.state.IndustriesState
import ru.practicum.android.diploma.features.filters.presentation.model.toDomain
import ru.practicum.android.diploma.features.filters.presentation.model.toUI
import ru.practicum.android.diploma.features.filters.presentation.model.ui.IndustryUI
import kotlin.coroutines.cancellation.CancellationException

class SpecializationSelectionViewModel(
    private val specializationInteractor: SpecializationInteractor
) : ViewModel() {
    private val _savedSelectedIndustry = MutableStateFlow<IndustryUI?>(null)
    val savedSelectedIndustry = _savedSelectedIndustry.asStateFlow()

    private var fullIndustriesList: List<IndustryUI> = emptyList()

    private val _industriesState = MutableStateFlow<IndustriesState>(IndustriesState.Loading)
    val industriesState = _industriesState.asStateFlow()

    init {
        specializationInteractor.clearManager()
    }

    fun updateSelectedIndustry(industry: IndustryUI) {
        specializationInteractor.setIndustry(industry.toDomain())
        _savedSelectedIndustry.value = industry
    }

    fun loadSavedIndustry() {
        viewModelScope.launch {
            specializationInteractor.getSavedIndustry()?.toUI()?.let {
                _savedSelectedIndustry.value = it
            }
        }
    }

    fun getIndustries() {
        viewModelScope.launch {
            specializationInteractor.getIndustriesList(mapOf())
                .onSuccess { list ->
                    fullIndustriesList = list.map { it.toUI() }

                    _industriesState.value = IndustriesState.Content(fullIndustriesList)
                }
                .onFailure { handleError(it) }
        }
    }

    fun acceptChanges() {
        specializationInteractor.acceptData()
    }

    @Suppress("LabeledExpression")
    fun search(text: String) {
        viewModelScope.launch {
            val filteredList = if (text.isBlank()) {
                fullIndustriesList
            } else {
                if (fullIndustriesList.isEmpty()) {
                    specializationInteractor.getIndustriesList(mapOf())
                        .onSuccess { list ->
                            fullIndustriesList = list.map { it.toUI() }

                            _industriesState.value = IndustriesState.Content(fullIndustriesList)
                        }
                        .onFailure { handleError(it) }
                }
                fullIndustriesList.filter { item ->
                    item.name?.contains(text, ignoreCase = true) ?: false
                }
            }
            if (fullIndustriesList.isEmpty()) return@launch
            _industriesState.value = IndustriesState.Content(filteredList)
        }
    }

    private fun handleError(error: Throwable) {
        when (error) {
            is CustomException.RequestError, CustomException.NetworkError, CustomException.ServerError -> {
                _industriesState.value = IndustriesState.Error
            }

            is CancellationException -> throw error
            else -> Unit
        }
    }
}
