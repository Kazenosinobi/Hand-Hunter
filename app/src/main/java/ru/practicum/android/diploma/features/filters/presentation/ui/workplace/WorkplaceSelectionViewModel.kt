package ru.practicum.android.diploma.features.filters.presentation.ui.workplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.features.filters.domain.api.workplace.SelectWorkplaceInteractor
import ru.practicum.android.diploma.features.filters.presentation.model.state.WorkplaceLocationState
import ru.practicum.android.diploma.features.filters.presentation.model.ui.WorkplaceLocationUI

class WorkplaceSelectionViewModel(
    private val interactor: SelectWorkplaceInteractor,
) : ViewModel() {

    private val workplaceLocationState = MutableStateFlow<WorkplaceLocationState>(WorkplaceLocationState.Init)
    fun getWorkplaceLocationState() = workplaceLocationState.asStateFlow()

    fun isWorkPlaceShowNeeded() {
        getLocation().takeIf { it.country.isNotBlank() || it.city.isNotBlank() }?.let {
            updateWorkplaceLocationState(it)
        }
    }

    fun getLocation(): WorkplaceLocationUI {
        val locationData = interactor.getFullLocationData()
        return WorkplaceLocationUI(
            country = locationData.country.name,
            city = locationData.region.name
        )
    }

    fun deleteCountryData() {
        viewModelScope.launch {
            interactor.deleteCountryData()
            updateWorkplaceLocationState(getLocation())
        }
    }

    fun deleteRegionData() {
        viewModelScope.launch {
            interactor.deleteRegionData()
            updateWorkplaceLocationState(getLocation())
        }
    }

    private fun updateWorkplaceLocationState(location: WorkplaceLocationUI) {
        viewModelScope.launch {
            workplaceLocationState.emit(WorkplaceLocationState.Content(location))
        }
    }
}
