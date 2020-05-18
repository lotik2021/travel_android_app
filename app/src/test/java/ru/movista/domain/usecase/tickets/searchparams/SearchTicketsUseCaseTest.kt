package ru.movista.domain.usecase.tickets.searchparams

import org.junit.Test

class SearchTicketsUseCaseTest {

//    val searchTicketsUseCase = SearchTicketsUseCase(object : ISearchParamsRepository {
//        override fun setPassengersInfo() {
//        }
//
//        override fun setTripTypes() {
//        }
//
//        override fun getEmptySearchModel(): SearchModel {
//            return SearchModel()
//        }
//
//        override fun search(chars: String): Single<Unit> {
//            return Single.just(Unit)
//        }
//
//        override fun getPlaceInfo(latitude: Double, longitude: Double): Single<Unit> {
//            return Single.just(Unit)
//        }
//    })

    @Test
    fun passenger_use_case_work() {
//        val model = searchTicketsUseCase.getInitialPassengersInfoModel()
//
//        searchTicketsUseCase.increaseAdultCount()
//        assertFalse(model.maxCountReached)
//        searchTicketsUseCase.increaseAdultCount()
//        assertFalse(model.maxCountReached)
//        searchTicketsUseCase.increaseChildrenCount(1, true)
//        assertTrue(model.maxCountReached)
//        searchTicketsUseCase.decreaseAdultCount()
//        assertFalse(model.maxCountReached)
//        searchTicketsUseCase.decreaseChildrenCount()
//        assertEquals(2, model.passengers.size)
//        assertTrue(model.isMinChildCount)
//        searchTicketsUseCase.decreaseChildrenCount()
//        searchTicketsUseCase.decreaseAdultCount()
//        assertTrue(model.isMinAdultCount)
//        searchTicketsUseCase.decreaseAdultCount()
//        searchTicketsUseCase.increaseChildrenCount(3, false)
//        searchTicketsUseCase.updateChildInfo(0, 5, true)
//        assertEquals(5, model.passengers.filterIsInstance<ChildPassenger>().get(0).age)
//        assertTrue(model.passengers.filterIsInstance<ChildPassenger>().get(0).isSeatRequired)
//        searchTicketsUseCase.increaseChildrenCount(8, true)
//        searchTicketsUseCase.increaseAdultCount()
//        searchTicketsUseCase.increaseAdultCount()
//        assertEquals(2, model.passengers.filterIsInstance<AdultPassenger>().size)
//        assertEquals(2, model.passengers.filterIsInstance<ChildPassenger>().size)
//
//        println(model)
    }

    @Test
    fun requestPassengersModel() {

//        val initModel = searchTicketsUseCase.getInitialPassengersInfoModel()
//        assertEquals(1, initModel.passengers.size)
    }

    @Test
    fun increaseAdultCount() {
//        searchTicketsUseCase.increaseAdultCount()
//        searchTicketsUseCase.increaseAdultCount()
//        searchTicketsUseCase.increaseAdultCount()
//        searchTicketsUseCase.increaseAdultCount()
//        assertFalse(model.maxCountReached)
    }

    @Test
    fun decreaseAdultCount() {
//        increaseAdultCount()
//        searchTicketsUseCase.decreaseAdultCount()
//        searchTicketsUseCase.decreaseAdultCount()
//        searchTicketsUseCase.decreaseAdultCount()
//        assertTrue(model.isMinAdultCount)
    }

    @Test
    fun increaseChildrenCount() {
    }

    @Test
    fun decreaseChildrenCount() {
    }

    @Test
    fun notifyNewPassengersInfoConfirmed() {
    }

    @Test
    fun updateChildInfo() {
    }
}