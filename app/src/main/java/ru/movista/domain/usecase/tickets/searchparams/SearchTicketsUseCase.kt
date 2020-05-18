package ru.movista.domain.usecase.tickets.searchparams

import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.threeten.bp.LocalDate
import ru.movista.data.repository.tickets.SearchPlacesRepository
import ru.movista.domain.model.tickets.*
import timber.log.Timber

class SearchTicketsUseCase(
    searchRepository: ISearchParamsRepository,
    private val placesRepository: SearchPlacesRepository
) : ISearchTicketsUseCase,
    IDateSelectUseCase,
    IPlaceSelectUseCase,
    IPassengersUseCase,
    IComfortTypeUseCase {

    companion object {
        private const val MAX_PASSENGERS_COUNT = 4
        private const val MAX_CHILD_AGE = 17
        private const val MIN_CHILD_AGE = 0
        private const val MIN_CHILD_AGE_WITH_REQUIRED_SEAT = 7
    }

    private var searchModelPublishSubject: PublishSubject<SearchModel> = PublishSubject.create()

    private val searchModel = searchRepository.getEmptySearchModel()

    private lateinit var passengersInfo: PassengersInfo

    private lateinit var passengersInfoCopy: PassengersInfo

    /**
     * Инициализация модели поиска маршрутов со значениями по-умолчанию
     */
    init {
        initPassengersInfoParams()
    }

    override fun getSearchParams() = searchModel

    override fun getChosenPlaces(): Pair<Place?, Place?> {
        return searchModel.fromPlace to searchModel.toPlace
    }

    override fun searchPlaces(chars: String): Single<List<Place>> {

        return placesRepository.searchPlaces(chars)
    }

    override fun setFromPlace(place: Place) {
        searchModel.fromPlace = place
    }

    override fun setToPlace(place: Place) {
        searchModel.toPlace = place
    }

    override fun swapPlaces() {
        val fromPlace = searchModel.fromPlace?.copy()

        searchModel.fromPlace = searchModel.toPlace
        searchModel.toPlace = fromPlace
    }

    override fun getInitialDates(): TripDates {
        return TripDates(searchModel.departureDate, searchModel.arrivalDate)
    }

    override fun setTripDate(from: LocalDate, to: LocalDate?) {
        searchModel.departureDate = from
        searchModel.arrivalDate = to
    }

    override fun getInitialPassengersInfoModel(): PassengersInfo {
        val passengersCopy = copyPassengersList(passengersInfo)
        passengersInfoCopy = passengersInfo.copy(passengers = passengersCopy) // сохраняем локально начальное состояние
        return passengersInfoCopy
    }


    override fun increaseAdultCount() {

        if (passengersInfoCopy.maxCountReached) return

        val adults = getAdults()
        adults.add(AdultPassenger())
        updateAdults(adults)

        checkIfMaxCountReached()
        passengersInfoCopy.isMinAdultCount = false
    }

    override fun decreaseAdultCount() {

        if (passengersInfoCopy.isMinAdultCount) return

        val adults = getAdults()
        if (adults.isNotEmpty()) adults.removeAt(adults.size - 1)
        updateAdults(adults)

        checkIfMinAdultCountReached(adults)
        passengersInfoCopy.maxCountReached = false

    }

    override fun increaseChildrenCount(childAge: Int, seatRequired: Boolean) {

        if (passengersInfoCopy.maxCountReached) return

        if (childAge > MAX_CHILD_AGE || childAge < MIN_CHILD_AGE) {
            Timber.e(IllegalStateException("Child's age should be in $MIN_CHILD_AGE .. $MAX_CHILD_AGE"))
            return
        }

        if (childAge >= MIN_CHILD_AGE_WITH_REQUIRED_SEAT && !seatRequired) {
            Timber.e(IllegalStateException("The seat is required for children above ${MIN_CHILD_AGE_WITH_REQUIRED_SEAT - 1} age"))
            return
        }

        val children = getChildren()

        children.add(ChildPassenger(childAge, seatRequired))
        updateChildren(children)

        checkIfMaxCountReached()
        passengersInfoCopy.isMinChildCount = false
    }

    override fun decreaseChildrenCount() {

        if (passengersInfoCopy.isMinChildCount) return

        val children = getChildren()

        children.removeAt(children.size - 1)

        updateChildren(children)

        checkIfMinChildrenCountReached(children)
        passengersInfoCopy.maxCountReached = false
    }

    override fun notifyNewPassengersInfoConfirmed() {
        with(passengersInfo) {
            passengers.clear()
            passengers.addAll(passengersInfoCopy.passengers)
            isMinAdultCount = passengersInfoCopy.isMinAdultCount
            isMinChildCount = passengersInfoCopy.isMinChildCount
            maxCountReached = passengersInfoCopy.maxCountReached
        }

        searchModel.passengers.clear()
        searchModel.passengers.addAll(passengersInfo.passengers)
    }

    override fun updateChildInfo(index: Int, age: Int, isSeatRequired: Boolean) {

        val childrenInfos = passengersInfoCopy.passengers.filter { it is ChildPassenger }
        val childInfo = childrenInfos.getOrNull(index) as? ChildPassenger
        childInfo?.age = age
        childInfo?.isSeatRequired = isSeatRequired
    }

    override fun setComfortType(comfortType: ComfortType) {
        searchModel.comfortType = comfortType
    }

    override fun isValidChildrenCount(): Boolean {
        with(passengersInfoCopy.passengers) {
            val adultPassengerCount = filterIsInstance<AdultPassenger>().count()
            val childPassengerCount = filterIsInstance<ChildPassenger>().count()
            return childPassengerCount <= adultPassengerCount
        }
    }

    private fun initPassengersInfoParams() {

        val passenger = AdultPassenger() // 1 взрослый пассажир

        passengersInfo = PassengersInfo(
            arrayListOf(passenger),
            maxCountReached = false,
            isMinAdultCount = true,
            isMinChildCount = true
        ).apply {
            searchModel.passengers.addAll(passengers)
        }
    }

    private fun getPassengers(): ArrayList<Passenger> {
        return passengersInfoCopy.passengers
    }

    private fun getAdults(): ArrayList<Passenger> {
        val passengers = getPassengers()
        return passengers.filter { it is AdultPassenger } as ArrayList
    }

    private fun updateAdults(adults: ArrayList<Passenger>) {
        val passengers = getPassengers()
        passengers.removeAll { it is AdultPassenger }
        passengers.addAll(adults)
    }

    private fun getChildren(): ArrayList<Passenger> {
        val passengers = getPassengers()
        return passengers.filter { it !is AdultPassenger } as ArrayList
    }

    private fun updateChildren(children: ArrayList<Passenger>) {
        val passengers = getPassengers()
        passengers.removeAll { it is ChildPassenger }
        passengers.addAll(children)
    }

    private fun searchModelSubjectCallOnNext() {
        searchModelPublishSubject.onNext(searchModel)
    }

    private fun checkIfMaxCountReached() {

        passengersInfoCopy.maxCountReached = passengersInfoCopy.passengers.size == MAX_PASSENGERS_COUNT
    }

    private fun checkIfMinAdultCountReached(adults: List<Passenger>) {
        passengersInfoCopy.isMinAdultCount = adults.size < 2
    }

    private fun checkIfMinChildrenCountReached(children: List<Passenger>) {
        passengersInfoCopy.isMinChildCount = children.isEmpty()
    }


    private fun copyPassengersList(passengersInfo: PassengersInfo): ArrayList<Passenger> {
        val passengersCopy = ArrayList<Passenger>()
        passengersInfo.passengers.forEach {
            when (it) {
                is AdultPassenger -> passengersCopy.add(it.copy() as Passenger)
                is ChildPassenger -> passengersCopy.add(it.copy() as Passenger)
            }
        }
        return passengersCopy
    }

}