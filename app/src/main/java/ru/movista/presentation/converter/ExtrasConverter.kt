package ru.movista.presentation.converter

import ru.movista.data.entity.ExtrasEntity
import ru.movista.di.FragmentScope
import ru.movista.presentation.viewmodel.ExtrasViewModel
import ru.movista.presentation.viewmodel.ToLocationExtrasViewModel
import ru.movista.presentation.viewmodel.UndefienedExtrasViewModel
import javax.inject.Inject

@FragmentScope
class ExtrasConverter @Inject constructor() {

    fun toExtrasViewModel(extrasEntity: ExtrasEntity): ExtrasViewModel {
        return when {
            extrasEntity.to_location != null -> ToLocationExtrasViewModel(
                extrasEntity.to_location.latitude,
                extrasEntity.to_location.longitude
            )
            else -> UndefienedExtrasViewModel
        }
    }
}