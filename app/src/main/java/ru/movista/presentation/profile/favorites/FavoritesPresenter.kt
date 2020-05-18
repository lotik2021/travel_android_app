package ru.movista.presentation.profile.favorites

import android.content.res.Resources
import moxy.InjectViewState
import ru.movista.R
import ru.movista.analytics.AnalyticsManager
import ru.movista.di.Injector
import ru.movista.domain.model.FavoritePlace
import ru.movista.domain.model.FavoritePlaceType
import ru.movista.domain.model.UserFavoritesPlaces
import ru.movista.domain.usecase.IChoosePlaceUseCase
import ru.movista.domain.usecase.ProfileUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.viewmodel.FavoriteModeViewModel
import ru.movista.utils.schedulersIoToMain
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class FavoritesPresenter(private val favorites: UserFavoritesPlaces) : BasePresenter<FavoritesView>() {
    override val screenTag: String
        get() = Screens.Favorites.TAG

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var profileUseCase: ProfileUseCase

    @Inject
    lateinit var resources: Resources

    override fun onPresenterInject() {
        Injector.init(Screens.Profile.TAG)
        Injector.profileComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.updateItems(favorites.homePlace, favorites.workPlace, favorites.otherFavoritesPlaces)
    }

    fun onBackClicked() {
        router.exit()
    }

    fun onAddFavoritesClick() {
        analyticsManager.reportAddFavoriteClick()
        goToSearchPlace(FavoritePlaceType.NONE)
    }

    fun onHomeEditClick(homePlaceId: Long?) {
        analyticsManager.reportHomeFavoriteEdit()
        goToSearchPlace(FavoritePlaceType.HOME, homePlaceId)
    }

    fun onWorkEditClick(workPlaceId: Long?) {
        analyticsManager.reportWorkFavoriteEdit()
        goToSearchPlace(FavoritePlaceType.WORK, workPlaceId)
    }

    private fun goToSearchPlace(favoritePlaceType: FavoritePlaceType, homeWorkPlaceId: Long? = null) {
        Injector.init(Screens.PlaceSearch.TAG) // инициализируем создание компонента, для последующей подписки на результат
        val choosePlaceUseCase = Injector.searchPlaceComponent?.getChoosePlaceUseCase() ?: return
        subscribeToPlaceFoundEvent(favoritePlaceType, choosePlaceUseCase)
        router.navigateTo(Screens.PlaceSearch(FavoriteModeViewModel(favoritePlaceType, homeWorkPlaceId)))
    }

    fun onFavoriteDeleteClick(favoritePlace: FavoritePlace) {
        addDisposable(
            profileUseCase.deleteUserFavoritesPlaces(favoritePlace.id)
                .schedulersIoToMain()
                .doOnSubscribe {
                    analyticsManager.reportDeletedFavoriteClick()
                    viewState.setFavoritesContentVisibility(false)
                    viewState.setLoaderVisibility(true)
                }
                .doAfterTerminate {
                    viewState.setFavoritesContentVisibility(true)
                    viewState.setLoaderVisibility(false)
                }
                .subscribe(
                    {
                        viewState.removeOtherFavorite(favoritePlace)
                        analyticsManager.reportDeletedFavorite(getFullAddress(favoritePlace))
                    },
                    { th ->
                        Timber.e("deleteUserFavoritesPlaces onError, ${th.message}")
                        viewState.showMsg(resources.getString(R.string.connection_error))
                    }
                )
        )
    }

    private fun subscribeToPlaceFoundEvent(
        favoritePlaceType: FavoritePlaceType,
        choosePlaceUseCase: IChoosePlaceUseCase
    ) {
        addDisposable(
            choosePlaceUseCase.getOnFavoritePlaceFoundObservable()
                .subscribe(
                    { favoritePlace ->
                        val fullAddress = getFullAddress(favoritePlace)

                        when (favoritePlaceType) {
                            FavoritePlaceType.HOME -> {
                                viewState.updateHome(favoritePlace)
                                analyticsManager.reportUpdatedHomeAddress(fullAddress)
                            }
                            FavoritePlaceType.WORK -> {
                                viewState.updateWork(favoritePlace)
                                analyticsManager.reportUpdatedWorkAddress(fullAddress)
                            }
                            FavoritePlaceType.NONE -> {
                                viewState.addOtherFavorite(favoritePlace)
                                analyticsManager.reportAddedFavoriteAddress(fullAddress)
                            }
                        }
                    },
                    { Timber.i("user abort choose place") }
                )
        )
    }

    private fun getFullAddress(favoritePlace: FavoritePlace): String {
        return "${favoritePlace.placeAddress.placeName}, ${favoritePlace.placeAddress.placeDescription}"
    }
}