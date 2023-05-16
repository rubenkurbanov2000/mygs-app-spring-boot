package cz.cvut.fel.mygs.controllers

import cz.cvut.fel.mygs.assemblers.*
import cz.cvut.fel.mygs.models.*
import cz.cvut.fel.mygs.repositories.UserRepository
import cz.cvut.fel.mygs.services.ManagerService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("/manager")
class ManagerController(
 val managerService: ManagerService,
 val gasStationAssembler: GasStationAssembler,
 val userRepository: UserRepository,
 val offerAssembler: OfferAssembler,
 val fuelAssembler: FuelAssembler,
 val refuelingStandAssembler: RefuelingStandAssembler


) {
    companion object {
        private val LOG = LoggerFactory.getLogger(ManagerController::class.java)
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/gs")
    fun createNewGasStation(@RequestBody gsData: AddNewGasStationData, fromUser: HttpServletRequest
    ): ResponseEntity<GasStation> {

        val manager = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)
        LOG.info(
            "REQUEST /manager/gs: Create new Gas Station '${gsData.name}' by manager - ${manager.email}"
        )

        val gs = managerService.createGasStation(gsData, manager)

        LOG.info(
            "RESPONSE /manager/gs: Create new Gas Station '${gsData.name}' by manager - ${manager.email} SUCCESS" +
                    "GS id = ${gs.id}"
        )
        return ResponseEntity.ok(gs)
    }


    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/gs/offer/{gsId}")
    fun addNewOfferToGasStation(@PathVariable gsId: Long,  @RequestBody offerData: AddNewOfferData, fromUser: HttpServletRequest
    ): ResponseEntity<OfferAssembler.OfferDto> {

        val manager = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)
        LOG.info(
            "REQUEST /manager/gs/offer/{gsId}: Create a offer for a gas station - ${gsId} by manager - ${manager.email}"
        )

        val offer = managerService.addNewOffer(gsId, offerData, manager)
        val response = offerAssembler.toOfferDto(offer)

        LOG.info(
            "RESPONSE /manager/gs/offer/{gsId}: Create a offer for a gas station - ${gsId} by manager - ${manager.email} SUCCESS " +
                    "with response ${offer.toString()}"
        )
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/gs/offer/{gsId}")
    fun deleteOfferFromGasStation(@PathVariable gsId: Long, @RequestBody offerData: DeleteOfferRequest, fromUser: HttpServletRequest
    ): ResponseEntity<OfferAssembler.OfferDto> {

        val manager = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /manager/gs/offer/{gsId}: Delete the offer from a gas station - $gsId by manager - ${manager.email}"
        )

        val offer = managerService.deleteOffer(gsId, offerData.offer_id, manager)
        val response = offerAssembler.toOfferDto(offer)

        LOG.info(
            "RESPONSE /manager/gs/offer/{gsId}: Delete the offer from a gas station - $gsId by manager - ${manager.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/gs/schedule-add/{gsId}")
    fun createSchedule(@PathVariable gsId: Long, @RequestBody schedule: AddScheduleRequest, fromUser: HttpServletRequest
    ): ResponseEntity<String> {

        val manager = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /manager/gs/schedule-add/{gsId}: Add schedule for a gas station - $gsId by manager - ${manager.email}"
        )

        managerService.addWorkingHours(gsId, schedule.schedule, manager)

        LOG.info(
            "RESPONSE /manager/gs/schedule-add/{gsId}: Add schedule for a gas station - $gsId by manager - ${manager.email} SUCCESS"
        )
        return ResponseEntity.ok("Success created!")
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/gs/schedule/{gsId}")
    fun changeScheduleInGasStation(@PathVariable gsId: Long, @RequestBody schedule: ScheduleRequest, fromUser: HttpServletRequest
    ): ResponseEntity<GasStation> {

        val manager = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /manager/gs/schedule/{gsId}: Change schedule for a gas station - $gsId by manager - ${manager.email}"
        )

        val response = managerService.changeSchedule(gsId, schedule,manager)

        LOG.info(
            "RESPONSE /manager/gs/schedule/{gsId}: Change schedule for a gas station - $gsId by manager - ${manager.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/gs/services/{gsId}")
    fun updateServicesListToGasStation(@PathVariable gsId: Long, @RequestBody data: UpdateServicesRequest, fromUser: HttpServletRequest
    ): ResponseEntity<GasStation> {

        val manager = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /manager/gs/services/{gsId}: Update services list  for a gas station - $gsId by manager - ${manager.email}"
        )

        val response = managerService.updateServicesList(gsId, data.services, manager)

        LOG.info(
            "RESPONSE /manager/gs/services/{gsId}: Update services list for a gas station - $gsId by manager - ${manager.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/gs/pm/{gsId}")
    fun updatePaymentMethodsListToGasStation(@PathVariable gsId: Long,
                                             @RequestBody data: UpdatePaymentMethodsRequest,
                                             fromUser: HttpServletRequest
    ): ResponseEntity<GasStation> {

        val manager = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /manager/gs/pm/{gsId}: Update payment methods list  for a gas station - $gsId by manager - ${manager.email}"
        )

        val response = managerService.updatePaymentMethodsList(gsId, data.paymentMethods, manager)

        LOG.info(
            "RESPONSE /manager/gs/pm/{gsId}: Update payment methods list for a gas station - $gsId by manager - ${manager.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/gs/rs/{gsId}")
    fun addRefuelingStandToGasStation(@PathVariable gsId: Long,
                                             @RequestBody data: AddRefuelingStandRequest,
                                             fromUser: HttpServletRequest
    ): ResponseEntity<RefuelingStandAssembler.RefuelingStandDto> {

        val manager = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /manager/gs/rs/{gsId}: Add refueling stand with number - ${data.stand_id} " +
                    "to a gas station - $gsId by manager - ${manager.email}"
        )

        val rs = managerService.addRefuelingStand(gsId, data.stand_id, manager)

        val response = refuelingStandAssembler.toRefuelingStandDto(rs)
        LOG.info(
            "RESPONSE /manager/gs/rs/{gsId}: Add refueling stand with number - ${data.stand_id} " +
                    "to a gas station - $gsId by manager - ${manager.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/gs/fuel-rs/{gsId}")
    fun addFuelToRefuelingStand(@PathVariable gsId: Long,
                                      @RequestBody data: ConnectRefStandAndFuelRequest,
                                      fromUser: HttpServletRequest
    ): ResponseEntity<RefuelingStandAssembler.RefuelingStandDto> {

        val manager = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /manager/gs/fuel-rs/{gsId}: Connect fuel with id - ${data.fuel_id} " +
                    "to refuel stand with id - ${data.stand_id} for a gas station - $gsId by manager - ${manager.email}"
        )

        val rs = managerService.addFuelToRefuelingStand(gsId, data, manager)
        val response = refuelingStandAssembler.toRefuelingStandDto(rs)

        LOG.info(
            "RESPONSE /manager/gs/fuel-rs/{gsId}: Connect fuel with id - ${data.fuel_id} " +
                    "to refuel stand with id - ${data.stand_id} for a gas station - $gsId by manager - ${manager.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/gs/fuel/{gsId}")
    fun addFuelToGasStation(@PathVariable gsId: Long,
                                @RequestBody fuelData: AddNewFuelRequest,
                                fromUser: HttpServletRequest
    ): ResponseEntity<FuelAssembler.FuelDto> {

        val manager = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /manager/gs/fuel/{gsId}: Add new fuel for a gas station - $gsId by manager - ${manager.email}"
        )

        val fuel = managerService.addFuelToGasStation(gsId, fuelData, manager)
        val response = fuelAssembler.toFuelDto(fuel)

        LOG.info(
            "RESPONSE /manager/gs/fuel/{gsId}: Add new fuel for a gas station - $gsId by manager - ${manager.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/gs/fuel-update/{gsId}")
    fun updateFuelPriceToGasStation(@PathVariable gsId: Long,
                            @RequestBody fuelData: UpdateFuelPriceRequest,
                            fromUser: HttpServletRequest
    ): ResponseEntity<FuelAssembler.FuelDto> {

        val manager = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /manager/gs/fuel-update/{gsId}: Update fuel price for a gas station - $gsId by manager - ${manager.email}"
        )

        val fuel = managerService.updateFuelPrice(gsId, fuelData, manager)
        val response = fuelAssembler.toFuelDto(fuel)

        LOG.info(
            "RESPONSE /manager/gs/fuel-update/{gsId}: Update fuel price for a gas station - $gsId by manager - ${manager.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }

}

data class AddNewGasStationData(
    val name: String,
    val services: String,
    val paymentMethods: String,
    val atitude: Double,
    val longtitude: Double,
    val buildingNumber: String,
    val city: String,
    val street: String,
    val zipCode: Int,
)


data class DeleteOfferRequest(
    val offer_id: Long,
)

data class UpdateServicesRequest(
    val services: String,
)
data class AddRefuelingStandRequest(
    val stand_id: Int
)

data class UpdatePaymentMethodsRequest(
    val paymentMethods: String,
)

data class ConnectRefStandAndFuelRequest(
    val stand_id: Int,
    val fuel_id: Long,
)

data class UpdateFuelPriceRequest(
    val fuel_id: Long,
    val price: Double
)


data class AddScheduleRequest(
    val schedule: List<ScheduleRequest>
)

data class AddNewOfferData(
    val content: String,
    val imgUrl: String,
    val title: String,
)

data class ScheduleRequest(
    val dayOfWeek: Int,
    val openFrom: LocalDateTime,
    val openTo: LocalDateTime,
    val isClosed: Boolean,
    val isAroundTheClock: Boolean
)

data class AddNewFuelRequest(
    val name: String,
    val price: Double,
    val fuelType: Int
)

