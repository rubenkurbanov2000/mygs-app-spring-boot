package cz.cvut.fel.mygs.controllers

import cz.cvut.fel.mygs.assemblers.GasStationAssembler
import cz.cvut.fel.mygs.assemblers.OfferAssembler
import cz.cvut.fel.mygs.models.*
import cz.cvut.fel.mygs.repositories.FuelRepository
import cz.cvut.fel.mygs.repositories.GasStationRepository
import cz.cvut.fel.mygs.repositories.RefuelingStandRepository
import cz.cvut.fel.mygs.repositories.UserRepository
import cz.cvut.fel.mygs.services.GasStationService
import org.slf4j.LoggerFactory
import org.springframework.data.geo.Distance
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/gas_station")
class GasStationController(
    val gasStationAssembler: GasStationAssembler,
    val gasStationRepository: GasStationRepository,
    val userRepository: UserRepository,
    val gasStationService: GasStationService,
    val fuelRepository: FuelRepository,
    val refuelingStandRepository: RefuelingStandRepository,
    val offerAssembler: OfferAssembler
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(GasStationController::class.java)
    }

    @GetMapping("/{city}")
    fun getAllGasStationsShortInfo(@PathVariable city: String, fromUser: HttpServletRequest): ResponseEntity<List<GasStationAssembler.ShortGSDto>> {
        LOG.info(
            "REQUEST /gas_station/{city}: Get all gas stations from city - $city"
        )
        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)
        val gs: List<GasStation> = gasStationService.getAllGasStations(city)

        val response: MutableList<GasStationAssembler.ShortGSDto> = mutableListOf()

        if(user != null){
            for (i in gs){
                response.add(
                    gasStationAssembler.toShortGsDto(
                        ShortGasStationInfo(
                            i,
                            user.favorite.contains(i),
                            5.7
                        )
                    )
                )

            }
        }else {
            for (i in gs) {
                response.add(
                    gasStationAssembler.toShortGsDto(
                        ShortGasStationInfo(
                            i,
                            false,
                            5.7
                        )
                    )
                )
            }
        }

        LOG.info(
            "RESPONSE /gas_station/{city}: Get all gas stations from city - $city with " +
                    "response - ${response.toString()}"
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/info/{gsId}")
    fun getGasStationFullInfo(@PathVariable gsId: Long, fromUser: HttpServletRequest): ResponseEntity<GasStationAssembler.FullGSDto>{
        LOG.info(
            "REQUEST /gas_station/{gs_id}: Get full information about the gas station with ID - $gsId"
        )
        val gs = gasStationService.getGasStationFullInfo(gsId)

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        val fuels = fuelRepository.findAllByGasStationId(gsId)
        val rs = refuelingStandRepository.findAllByGasStationId(gsId)

        println("fuels - ${fuels.size}, rs - ${rs.size}")

        val response = gasStationAssembler.toFullGsDto(
            FullGasStationInfo(
                gs,
                user?.favorite?.contains(gs) ?: false,
                5.7,
                fuels,
                rs
            )
        )

        LOG.info(
            "RESPONSE /gas_station/{gs_id}: Get full information about the gas station with ID - $gsId with" +
                    " response - ${response.toString()}"
        )

        return ResponseEntity.ok(response)

    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/favorite")
    fun getFavoriteGasStations(fromUser: HttpServletRequest): ResponseEntity<List<GasStationAssembler.ShortGSDto>>{

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /gas_station/favorite: Get a list of all favorite gas stations for a user - ${user.email}"
        )

        val gs = gasStationService.getFavoriteGasStations(user)
        val response: MutableList<GasStationAssembler.ShortGSDto> = mutableListOf()

        for (i in gs){
            response.add(
                gasStationAssembler.toShortGsDto(
                    ShortGasStationInfo(
                        i,
                        true,
                        5.7
                    )
                )
            )
        }

        LOG.info(
            "RESPONSE /gas_station/favorite: Get a list of all favorite gas stations for a user - ${user.email} " +
                    "with the number of elements found - ${response.size}"
        )

        return ResponseEntity.ok(response)

    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/favorite/{gs_id}")
    fun changeRelationshipToGasStation(@PathVariable gs_id: Long, fromUser: HttpServletRequest): ResponseEntity<GasStationAssembler.ShortGSDto>{
        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /gas_station/favorite/{gs_id}: Change the user's attitude - ${user.email}, to the gas station with ID - $gs_id}"
        )

        val gs = gasStationService.changeRelationshipToGasStation(gs_id, user)
        val response = gasStationAssembler.toShortGsDto(
            ShortGasStationInfo(
                gs,
                user?.favorite?.contains(gs) ?: false,
                5.7,
            )
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/filter")
    fun getFilteredGasStationList(@RequestBody filterProperties: FilterGsRequest, fromUser: HttpServletRequest): ResponseEntity<List<GasStationAssembler.ShortGSDto>>{
        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /gas_station/filter: Get a filtered list of gas stations with parameters - ${filterProperties.toString()}"
        )
        val gs = gasStationService.getFilteredGasStationList(filterProperties)

        val response: MutableList<GasStationAssembler.ShortGSDto> = mutableListOf()

        if(user != null){
            for (i in gs){
                response.add(
                    gasStationAssembler.toShortGsDto(
                        ShortGasStationInfo(
                            i,
                            user.favorite.contains(i),
                            5.7
                        )
                    )
                )

            }
        }else {
            for (i in gs) {
                response.add(
                    gasStationAssembler.toShortGsDto(
                        ShortGasStationInfo(
                            i,
                            false,
                            5.7
                        )
                    )
                )
            }
        }

        LOG.info(
            "RESPONSE /gas_station/filter: Get a filtered list of gas stations with parameters - ${filterProperties.toString()} " +
                    "with the number of elements found - ${response.size}"
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/search/{req}")
    fun searchGasStations(@PathVariable req: String, fromUser: HttpServletRequest): ResponseEntity<List<GasStationAssembler.ShortGSDto>>{

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /gas_station/search/{req): Get a list of gas stations with matching search - $req"
        )
        val gs = gasStationService.getGasStationsByRequestText(req)

        val response: MutableList<GasStationAssembler.ShortGSDto> = mutableListOf()

        if(user != null){
            for (i in gs){
                response.add(
                    gasStationAssembler.toShortGsDto(
                        ShortGasStationInfo(
                            i,
                            user.favorite.contains(i),
                            5.7
                        )
                    )
                )

            }
        }else {
            for (i in gs) {
                response.add(
                    gasStationAssembler.toShortGsDto(
                        ShortGasStationInfo(
                            i,
                            false,
                            5.7
                        )
                    )
                )
            }
        }

        LOG.info(
            "RESPONSE /gas_station/search/{req}: Get a list of gas stations with matching search - $req " +
                    "with the number of elements found - ${response.size}"
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/offers")
    fun getRandomFiveOffers(): ResponseEntity<List<OfferAssembler.OfferDto>>{

        LOG.info(
            "REQUEST /gas_station/offers: Get a list of 5 random offers from gas stations"
        )
        val offers = gasStationService.getOffers()
        val response = offerAssembler.toListOfferDto(offers)

        LOG.info(
            "RESPONSE /gas_station/offers: Get a list of 5 random offers from gas stations SUCCESS"
        )

        return ResponseEntity.ok(response)

    }

    @GetMapping("/nearest")
    fun getNearestGasStation(): ResponseEntity<GasStationAssembler.ShortGSDto>{

        LOG.info(
            "REQUEST /gas_station/nearest: Get the nearest gas station"
        )
        val gs = gasStationService.getNearestGS()
        val response = gasStationAssembler.toShortGsDto(ShortGasStationInfo(
            gs,
            false,
            5.7
        ))

        LOG.info(
            "RESPONSE /gas_station/nearest: Get the nearest gas station with result - ${response.toString()}"
        )

        return ResponseEntity.ok(response)

    }

}

data class ShortGasStationInfo(
    val gs: GasStation,
    val isFavorite: Boolean,
    val distance: Double,
)

data class FilterGsRequest(
    val fuelTypes: List<Int>,
    val paymentMethods: List<Int>,
    val services: List<Int>,
    val city: String
) {
    override fun toString(): String {
        return "FilterGsRequest(fuelTypes=$fuelTypes, paymentMethods=$paymentMethods, services=$services, city='$city')"
    }
}

data class FullGasStationInfo(
    val gs: GasStation,
    val isFavorite: Boolean,
    val distance: Double,
    val priceList: List<Fuel>,
    val refuelingStands: List<RefuelingStand>
)

