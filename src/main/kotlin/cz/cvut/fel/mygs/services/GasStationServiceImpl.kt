package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.exceptios.GasStationIsNotFoundException
import cz.cvut.fel.mygs.controllers.FilterGsRequest
import cz.cvut.fel.mygs.models.Fuel
import cz.cvut.fel.mygs.models.GasStation
import cz.cvut.fel.mygs.models.Offer
import cz.cvut.fel.mygs.models.User
import cz.cvut.fel.mygs.repositories.FuelRepository
import cz.cvut.fel.mygs.repositories.GasStationRepository
import cz.cvut.fel.mygs.repositories.OfferRepository
import cz.cvut.fel.mygs.repositories.UserRepository
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class GasStationServiceImpl(
    val gasStationRepository: GasStationRepository,
    val userRepository: UserRepository,
    val fuelRepository: FuelRepository,
    val offerRepository: OfferRepository
):GasStationService {

    override fun getAllGasStations(city: String): List<GasStation> {
        return gasStationRepository.findAllByCity(city)
    }

    @Throws(GasStationIsNotFoundException::class)
    override fun getGasStationFullInfo(gs_id: Long): GasStation {
        println("try find $gs_id")
        return gasStationRepository.findGasStationById(gs_id).orElseThrow{ GasStationIsNotFoundException() }
    }

    override fun getFavoriteGasStations(user: User): List<GasStation> {
        return user.favorite
    }

    @Throws(GasStationIsNotFoundException::class)
    override fun changeRelationshipToGasStation(gs_id: Long, user: User): GasStation {

        val gs = gasStationRepository.findById(gs_id).orElseThrow{ GasStationIsNotFoundException() }

        if (user.favorite.contains(gs))
            user.favorite.remove(gs)
        else
            user.favorite.add(gs)

        userRepository.save(user)

        return gs
    }


    override fun getFilteredGasStationList(filterProperties: FilterGsRequest): List<GasStation> {

        val allGS = getAllGasStations(filterProperties.city)
        val filteredGsList: MutableList<GasStation> = allGS.toMutableList()

        //Filter by fuel type
        for(i in filteredGsList){
            val fuels = fuelRepository.findAllByGasStationId(i.id).toMutableList()
            val fuelsIdList: List<Int> = getListWithFuelsTypeId(fuels)

            for(f in filterProperties.fuelTypes){
                if (!fuelsIdList.contains(f)){
                    filteredGsList.remove(i)
                    break
                }
            }
        }

        //Filter by services
        for(i in filteredGsList){
            for(f in filterProperties.services){
                if (!i.getServices().contains(f)){
                    filteredGsList.remove(i)
                    break
                }
            }
        }

        //Filter by payment method
        for(i in filteredGsList){
            for(f in filterProperties.paymentMethods){
                if (!i.getPaymentMethods().contains(f)){
                    filteredGsList.remove(i)
                    break
                }
            }
        }

        return filteredGsList
    }

    override fun getGasStationsByRequestText(text: String): List<GasStation> {
        return gasStationRepository.findGasStationByCoincidence(text)
    }

    override fun getOffers(): List<Offer> {
        val offers = offerRepository.findAll()
        val justFive: MutableList<Offer> = mutableListOf()

        while (justFive.size != 5){
            val random = offers.random()
            if(!justFive.contains(random) and !random.isDeleted ) justFive.add(random)
        }
        return justFive
    }

    override fun getNearestGS(): GasStation {
        val gs = gasStationRepository.findAll()
        return gs.random()
    }

    fun getListWithFuelsTypeId(fuels: List<Fuel>):List<Int>{
        val list:MutableList<Int> = mutableListOf()

        for(i in fuels){
            list.add(i.fuelTypeFilterToId())
        }
        return list
    }
}