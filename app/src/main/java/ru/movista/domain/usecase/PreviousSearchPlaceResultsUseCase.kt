package ru.movista.domain.usecase

import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.movista.data.repository.SearchPlaceResultsRepository
import ru.movista.di.FragmentScope
import ru.movista.domain.model.FavouritePlace
import ru.movista.domain.model.PlaceSearchHistoryItem
import ru.movista.domain.model.RecentPlace
import ru.movista.domain.model.UserPlacesHistory
import javax.inject.Inject

@FragmentScope
class PreviousSearchPlaceResultsUseCase @Inject constructor(
    private val searchPlaceResultsRepository: SearchPlaceResultsRepository
) {

    companion object {
        private const val FAVOURITES_REQUEST_COUNT = 5
        private const val RECENTS_REQUEST_COUNT = 10
    }

    /**
     * запрашиваем избранные и недвание места
     * возвращаем общим списком
     */
    fun getUserSearchPlacePreviousResults(): Single<List<PlaceSearchHistoryItem>> {
        return searchPlaceResultsRepository.getFavourites(FAVOURITES_REQUEST_COUNT)
            .zipWith(
                searchPlaceResultsRepository.getRecents(RECENTS_REQUEST_COUNT),
                BiFunction<List<FavouritePlace>, List<RecentPlace>, List<PlaceSearchHistoryItem>> { favResponse, recentsResponse ->
                    val combinedList = arrayListOf<PlaceSearchHistoryItem>()
                    combinedList.addAll(favResponse) // сначала избранные, важно сохранить порядок
                    combinedList.addAll(recentsResponse)
                    return@BiFunction combinedList
                }
            )
    }

    /**
     * запрашиваем избранные и недвание места
     * возвращаем UserPlacesHistory
     */
    fun getUserPlacesHistory(): Single<UserPlacesHistory> {
        return searchPlaceResultsRepository.getFavourites(FAVOURITES_REQUEST_COUNT)
            .zipWith(
                searchPlaceResultsRepository.getRecents(RECENTS_REQUEST_COUNT),
                BiFunction<List<FavouritePlace>, List<RecentPlace>, UserPlacesHistory> { favourites, recent ->
                    return@BiFunction UserPlacesHistory(
                        favourites,
                        recent
                    )
                }
            )
    }
}