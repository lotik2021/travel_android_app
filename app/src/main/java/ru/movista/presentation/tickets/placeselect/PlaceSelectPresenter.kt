package ru.movista.presentation.tickets.placeselect

import android.content.res.Resources
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import moxy.InjectViewState
import ru.movista.R
import ru.movista.di.Injector
import ru.movista.domain.model.tickets.Place
import ru.movista.domain.usecase.tickets.searchparams.IPlaceSelectUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.movista.presentation.utils.postOnMainThreadWithDelay
import ru.movista.utils.schedulersIoToMain
import ru.movista.utils.toDefaultLowerCase
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class PlaceSelectPresenter(citySelectTypeName: String) : BasePresenter<PlaceSelectView>() {

    override val screenTag: String
        get() = Screens.PlaceSelect.TAG

    private val searchDisposable = CompositeDisposable()

    private var placeSelectType: PlaceSelectType = PlaceSelectType.valueOf(citySelectTypeName)

    private var chosenFromPlace: Place? = null
    private var chosenToPlace: Place? = null

    private var fromPlaceOnceSelected = false
    private var toPlaceOnceSelected = false

    private lateinit var currentQuery: String

    private lateinit var currentSearchResult: List<Place>

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var placeSelectUseCase: IPlaceSelectUseCase

    @Inject
    lateinit var resources: Resources

    override fun onPresenterInject() {
        super.onPresenterInject()
        Injector.init(screenTag)
        Injector.placeSelectComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        updateState()
    }

    fun onBackPressed() {
        exitDelayed()
    }


    fun onSearchTextChanged(query: String) {
        if (query.isEmpty()) {
            viewState.hideErrorMsg()
            currentSearchResult = emptyList()
            viewState.showPlacesFound(currentSearchResult)
            return
        }

        currentQuery = query
        doSearchRequest()
    }

    fun onPlaceSelected(index: Int) {
        clearView()

        when (placeSelectType) {
            PlaceSelectType.FROM -> {
                if (!fromPlaceOnceSelected) {
                    setFromPlaceOnce(index)
                } else {
                    setPlacesWithDefaultBehaviour(index)
                }
            }
            PlaceSelectType.TO -> {
                if (!toPlaceOnceSelected) {
                    setToPlaceOnce(index)
                } else {
                    setPlacesWithDefaultBehaviour(index)
                }
            }
        }
    }

    private fun updateState() {
        requestChosenPlaces()
        setToolbarText()
    }

    private fun requestChosenPlaces() {
        val (fromPlace, toPlace) = placeSelectUseCase.getChosenPlaces()
        chosenFromPlace = fromPlace
        chosenToPlace = toPlace
    }

    private fun setToolbarText() {
        val fromText = chosenFromPlace?.name?.toDefaultLowerCase()
            ?: resources.getString(R.string.from).toDefaultLowerCase()
        val toText = chosenToPlace?.name?.toDefaultLowerCase()
            ?: resources.getString(R.string.to).toDefaultLowerCase()

        viewState.setToolbarText(fromText, toText)
    }

    private fun doSearchRequest() {
        searchDisposable.clear()

        searchDisposable.add(
            placeSelectUseCase.searchPlaces(currentQuery)
                .schedulersIoToMain()
                .doOnSubscribe {
                    viewState.showLoading()
                    viewState.hideErrorMsg()
                }
                .doAfterTerminate { viewState.hideLoading() }
                .subscribe(
                    { onPlacesFound(it) },
                    { th ->
                        Timber.e(th, "SearchPlaces failed")
                        viewState.showErrorMsg(
                            getErrorDescription(th),
                            View.OnClickListener { doSearchRequest() }
                        )
                    }
                )
        )
    }

    private fun onPlacesFound(places: List<Place>) {
        viewState.showRecyclerView()
        currentSearchResult = places
        viewState.showPlacesFound(currentSearchResult)
    }

    private fun clearView() {
        viewState.hideRecyclerView()
        viewState.clearEditText()
    }

    private fun setFromPlaceOnce(index: Int) {
        placeSelectUseCase.setFromPlace(currentSearchResult[index])
        fromPlaceOnceSelected = true
        updateState()

        if (chosenToPlace != null) {
            exitDelayed()
        }
    }

    private fun setToPlaceOnce(index: Int) {
        placeSelectUseCase.setToPlace(currentSearchResult[index])
        toPlaceOnceSelected = true
        updateState()

        if (chosenFromPlace != null) {
            exitDelayed()
        }
    }

    private fun setPlacesWithDefaultBehaviour(index: Int) {
        if (chosenFromPlace == null) {
            placeSelectUseCase.setFromPlace(currentSearchResult[index])
            updateState()
        } else if (chosenToPlace == null) {
            placeSelectUseCase.setToPlace(currentSearchResult[index])
            updateState()
            exitDelayed()
        }
    }


    private fun exitDelayed() {
        viewState.hideKeyboard()
        postOnMainThreadWithDelay(150) { router.exit() }
    }
}