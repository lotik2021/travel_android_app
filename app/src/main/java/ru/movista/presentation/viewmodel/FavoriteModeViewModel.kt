package ru.movista.presentation.viewmodel

import ru.movista.domain.model.FavoritePlaceType
import java.io.Serializable

data class FavoriteModeViewModel (
    val placeType: FavoritePlaceType,
    val homeWorkPlaceId: Long?
) : Serializable