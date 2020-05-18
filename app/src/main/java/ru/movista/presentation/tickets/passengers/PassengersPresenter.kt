package ru.movista.presentation.tickets.passengers

import moxy.InjectViewState
import ru.movista.R
import ru.movista.di.Injector
import ru.movista.domain.model.tickets.PassengersInfo
import ru.movista.domain.usecase.tickets.searchparams.IPassengersUseCase
import ru.movista.presentation.Screens
import ru.movista.presentation.base.BasePresenter
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class PassengersPresenter : BasePresenter<PassengersView>() {

    override val screenTag: String
        get() = Screens.Passengers.TAG

    private var childEditingPosition: Int? = null

    private lateinit var passengersInfo: PassengersInfo

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var passengersUseCase: IPassengersUseCase

    @Inject
    lateinit var passengersConverter: PassengersConverter

    override fun onPresenterInject() {
        super.onPresenterInject()
        Injector.init(screenTag)
        Injector.passengersComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        passengersInfo = passengersUseCase.getInitialPassengersInfoModel()
        changeViewState()
    }

    fun onBackPressed() {
        router.exit()
    }

    fun onIncreaseAdultCountClicked() {
        if (passengersInfo.maxCountReached) return

        passengersUseCase.increaseAdultCount()
        changeViewState()
    }

    fun onDecreaseAdultCountClicked() {
        if (passengersInfo.isMinAdultCount) return

        passengersUseCase.decreaseAdultCount()
        changeViewState()
    }

    fun onIncreaseChildrenCountClicked() {
        if (passengersInfo.maxCountReached) return

        this.childEditingPosition = null

        viewState.showChildInfoDialog(null)
    }

    fun onChildInfoDoneButtonClicked(chosenAge: Int, seatRequired: Boolean) {
        val childEditingPosition = childEditingPosition

        if (childEditingPosition != null) {
            // изменили данные ребенка
            passengersUseCase.updateChildInfo(childEditingPosition, chosenAge, seatRequired)
        } else {
            // добавили ребенка
            passengersUseCase.increaseChildrenCount(chosenAge, seatRequired)
        }

        this.childEditingPosition = null

        changeViewState()
    }

    fun onDecreaseChildrenCountClicked() {
        if (passengersInfo.isMinChildCount) return

        passengersUseCase.decreaseChildrenCount()
        changeViewState()
    }

    fun onChildInfoChipClicked(position: Int) {
        childEditingPosition = position

        val passengersInfoViewModel = passengersConverter.toPassengersInfoViewModel(passengersInfo)
        viewState.showChildInfoDialog(passengersInfoViewModel.childrenInfo[position])
    }

    fun onDoneButtonClicked() {
        if (passengersUseCase.isValidChildrenCount()) {
            passengersUseCase.notifyNewPassengersInfoConfirmed()
            router.exit()
        } else {
            viewState.showMsg(R.string.child_info_alert)
        }
    }

    private fun changeViewState() {
        viewState.showPassengersInfo(passengersConverter.toPassengersInfoViewModel(passengersInfo))
        disableChangesIfNeeded(passengersInfo)
    }

    private fun disableChangesIfNeeded(passengersInfo: PassengersInfo) {
        viewState.disableIncreaseAdultCountIf(passengersInfo.maxCountReached)
        viewState.disableIncreaseChildrenCountIf(passengersInfo.maxCountReached)
        viewState.disableDecreaseAdultCountIf(passengersInfo.isMinAdultCount)
        viewState.disableDecreaseChildrenCountIf(passengersInfo.isMinChildCount)
    }
}