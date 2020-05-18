package ru.movista.data.entity.tickets

import ru.movista.data.entity.tickets.common.Error
import ru.movista.data.entity.tickets.common.SearchParams

class AsyncSearchRequest(val searchParams: SearchParams)


class AsyncSearchResponse(
    val data: AsyncSearchData,
    val errors: List<Error>?
)

class AsyncSearchData(
    val uid: String,
    val dateTimeUpdate: String,
    val isComplete: Boolean
)
