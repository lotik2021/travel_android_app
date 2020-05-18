package ru.movista.presentation.profile.transporttypes

import ru.movista.domain.model.ProfileTransportType
import ru.movista.presentation.base.BaseLoadingView

interface TransportTypesView : BaseLoadingView {
    fun setTransportTypes(transportsTypes: List<ProfileTransportType>)
}