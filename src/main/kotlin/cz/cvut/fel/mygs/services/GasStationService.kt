package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.controllers.FilterGsRequest
import cz.cvut.fel.mygs.models.GasStation
import cz.cvut.fel.mygs.models.Offer
import cz.cvut.fel.mygs.models.User

interface GasStationService {

    fun getAllGasStations(city: String): List<GasStation>
    fun getGasStationFullInfo(gs_id: Long): GasStation
    fun getFavoriteGasStations(user: User): List<GasStation>
    fun changeRelationshipToGasStation(gs_id: Long, user: User):GasStation
    fun getFilteredGasStationList(filterProperties: FilterGsRequest): List<GasStation>
    fun getGasStationsByRequestText(text: String):List<GasStation>
    fun getOffers():List<Offer>
    fun getNearestGS(): GasStation
}