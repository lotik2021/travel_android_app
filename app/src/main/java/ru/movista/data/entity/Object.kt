package ru.movista.data.entity

import ru.movista.data.entity.objects.*
import ru.movista.data.source.local.ObjectType

sealed class ObjectEntity(
    val object_id: String
)

data class BotMessageEntity(
    val data: BotMessageDataEntity
) : ObjectEntity(ObjectType.BOT_MESSAGE)

data class ShortRoutesEntity(
    val data: ShortRoutesDataEntity
) : ObjectEntity(ObjectType.ROUTES_GOOGLE)

data class TaxiEntity(
    val data: TaxiDataEntity
) : ObjectEntity(ObjectType.TAXI)

data class CarEntity(
    val data: CarDataEntity
) : ObjectEntity(ObjectType.CAR)

data class LongRoutesEntity(
    val data: LongRoutesDataEntity
) : ObjectEntity(ObjectType.ROUTES_MOVISTA)

data class MovistaPlaceholderEntity(
    val data: MovistaPlaceholderDataEntity
) : ObjectEntity(ObjectType.PLACEHOLDER_MOVISTA)

data class AvailableTravelCardsEntity(
    val data: AvailableTravelCardsResponse
) : ObjectEntity(ObjectType.AVAILABLE_TRAVEL_CARDS)

data class SkillEntity(
    val data: SkillDataEntity
) : ObjectEntity(ObjectType.SKILL)

data class AddressesEntity(
    val data: AddressesDataEntity
) : ObjectEntity(ObjectType.ADDRESS)

data class WeatherEntity(
    val data: WeatherDataEntity
) : ObjectEntity(ObjectType.WEATHER)
