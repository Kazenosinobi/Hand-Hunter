package ru.practicum.android.diploma.di

import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.practicum.android.diploma.features.common.presentation.ResourceProvider
import ru.practicum.android.diploma.features.common.presentation.ResourceProviderImpl
import ru.practicum.android.diploma.features.filters.domain.manager.FilterManager
import ru.practicum.android.diploma.features.filters.domain.manager.FilterManagerImpl
import ru.practicum.android.diploma.features.filters.domain.manager.LocationManager
import ru.practicum.android.diploma.features.filters.domain.manager.LocationManagerImpl
import ru.practicum.android.diploma.features.filters.domain.manager.SpecializationManager
import ru.practicum.android.diploma.features.filters.domain.manager.SpecializationManagerImpl

val utilsModule = module {
    single<ResourceProvider> {
        ResourceProviderImpl(androidApplication())
    }

    singleOf(::FilterManagerImpl) bind FilterManager::class
    singleOf(::LocationManagerImpl) bind LocationManager::class
    singleOf(::SpecializationManagerImpl) bind SpecializationManager::class
}
