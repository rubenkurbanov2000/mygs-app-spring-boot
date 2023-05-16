package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.controllers.*
import cz.cvut.fel.mygs.models.*

interface ManagerService {

    fun createGasStation(gsData: AddNewGasStationData, manager: User): GasStation
    fun addWorkingHours(gs_id: Long, schedules: List<ScheduleRequest>, manager: User)
    fun addNewOffer(gs_id: Long, offerData: AddNewOfferData, manager: User): Offer
    fun deleteOffer(gs_id: Long, offer_id: Long, manager: User): Offer
    fun changeSchedule(gs_id: Long, schedule: ScheduleRequest, manager: User): GasStation
    fun updateServicesList(gs_id: Long, services: String, manager: User): GasStation
    fun updatePaymentMethodsList(gs_id: Long, pM: String, manager: User): GasStation
    fun addRefuelingStand(gs_id: Long, stand_id: Int, manager: User): RefuelingStand
    fun addFuelToRefuelingStand(gs_id: Long,data: ConnectRefStandAndFuelRequest, manager: User): RefuelingStand
    fun addFuelToGasStation(gs_id: Long, fuelData: AddNewFuelRequest, manager: User): Fuel
    fun updateFuelPrice(gs_id: Long, fuelData: UpdateFuelPriceRequest, manager: User): Fuel
}
