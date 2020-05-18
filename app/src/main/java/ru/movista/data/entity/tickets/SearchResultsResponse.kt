package ru.movista.data.entity.tickets

import ru.movista.data.entity.tickets.common.BasePathGroupEntity
import ru.movista.data.entity.tickets.common.Error
import ru.movista.data.entity.tickets.common.SearchParams

class SearchStatusRequest(val uid: String)


class SearchResultsResponse(
    val data: SearchResultsData,
    val errors: List<Error>?
)

class SearchResultsData(
    val pathGroups: List<BasePathGroupEntity>,
    val searchParams: SearchResultsParams?,
    val expirationTime: String,
    val lifeTime: Long
)

class SearchResultsParams(
    val minChangeDuration: Long,
    val maxChangeDuration: Long,
    val maxChanges: Long,
    val maxResultRoutesCount: Long,
    val bookableOnly: Boolean,
    val preferBookable: Boolean,
    val partnerCode: String
) : SearchParams()
