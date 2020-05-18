package ru.movista.presentation.chat

import ru.movista.presentation.viewmodel.*
import java.io.Serializable

sealed class ChatItem : Serializable

data class BotMessage(
    val message: String
) : ChatItem(), Serializable

data class UserMessage(
    val message: String
) : ChatItem(), Serializable

object LoadingBotMessage : ChatItem()

class MapItem(
    val latFrom: Double? = null,
    val lonFrom: Double? = null,
    val latTo: Double? = null,
    val lonTo: Double? = null
) : ChatItem(), Serializable

data class TaxiItem(
    val taxiProviders: List<TaxiProviderViewModel>
) : ChatItem(), Serializable

data class RoutesItem(
    val routes: List<RouteViewModel>
) : ChatItem(), Serializable

data class CarItem(
    val carRoutes: List<CarRouteViewModel>
) : ChatItem(), Serializable

data class LongRoutesItem(
    val longRoutes: List<MovistaRouteViewModel>
) : ChatItem(), Serializable

data class MovistaPlaceholderItem(
    val placeholder: MovistaPlaceholderViewModel
) : ChatItem(), Serializable

data class SkillsItem(
    val skills: MutableList<SkillViewModel>
) : ChatItem(), Serializable

data class AddressesItem(
    val addresses: List<ChatAddressViewModel>,
    val googleToken: String
) : ChatItem(), Serializable

data class WeatherItem(
    val weatherViewModel: WeatherViewModel
) : ChatItem(), Serializable
