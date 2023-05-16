package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.exceptios.FuelIsNotFoundException
import cz.cvut.fel.mygs.exceptios.GasStationDoesNotBelongToManagerException
import cz.cvut.fel.mygs.exceptios.GasStationIsNotFoundException
import cz.cvut.fel.mygs.exceptios.GasStationNameIsEmptyException
import cz.cvut.fel.mygs.exceptios.GasStationPaymentMethodsIsEmptyException
import cz.cvut.fel.mygs.exceptios.GasStationServicesIsEmptyException
import cz.cvut.fel.mygs.exceptios.OfferIsNotFoundException
import cz.cvut.fel.mygs.exceptios.RefuelingStandAlreadyExistException
import cz.cvut.fel.mygs.controllers.*
import cz.cvut.fel.mygs.models.*
import cz.cvut.fel.mygs.repositories.*
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class ManagerServiceImpl(
    val managerRepository: ManagerRepository,
    val gasStationRepository: GasStationRepository,
    val offerRepository: OfferRepository,
    val refuelingStandRepository: RefuelingStandRepository,
    val fuelRepository: FuelRepository,
    val feedBackRepository: FeedBackRepository,
    val scheduleRepository: ScheduleRepository,
):ManagerService {

    @Throws(GasStationNameIsEmptyException::class, GasStationPaymentMethodsIsEmptyException::class, GasStationServicesIsEmptyException::class)
    override fun createGasStation(gsData: AddNewGasStationData, manager: User): GasStation {

        if(gsData.name.isEmpty()) throw GasStationNameIsEmptyException()
        if(gsData.services.isEmpty()) throw GasStationServicesIsEmptyException()
        if(gsData.paymentMethods.isEmpty()) throw GasStationPaymentMethodsIsEmptyException()

        val newGs = GasStation(
            name = gsData.name,
            servicesListString = gsData.services,
            paymentMethodsListString = gsData.paymentMethods,
            manager = manager,
            atitude = gsData.atitude,
            longtitude = gsData.longtitude,
            buildingNumber = gsData.buildingNumber,
            city = gsData.city,
            street = gsData.street,
            zipCode = gsData.zipCode
        )
        gasStationRepository.save(newGs)

        return newGs

    }


    @Throws(GasStationIsNotFoundException::class, GasStationDoesNotBelongToManagerException::class)
    override fun addWorkingHours(gs_id: Long, schedules: List<ScheduleRequest>, manager: User) {

        val gs = gasStationRepository.findById(gs_id).orElseThrow{ GasStationIsNotFoundException() }
        if(!gs.manager!!.equals(manager)) throw GasStationDoesNotBelongToManagerException()

        for(i in schedules){

            val s = Schedule(
                dayOfWeek = i.dayOfWeek,
                openFrom = i.openFrom,
                openTo = i.openTo,
                isClosed = i.isClosed,
                isAroundTheClock = i.isAroundTheClock,
                gasStation = gs
            )
            scheduleRepository.save(s)
        }

    }

    @Throws(GasStationIsNotFoundException::class, GasStationDoesNotBelongToManagerException::class)
    override fun addNewOffer(gs_id: Long, offerData: AddNewOfferData, manager: User): Offer {

        val gs = gasStationRepository.findById(gs_id).orElseThrow{ GasStationIsNotFoundException() }
        if(!gs.manager!!.equals(manager)) throw GasStationDoesNotBelongToManagerException()

        val newOffer = Offer(
            content = offerData.content,
            title = offerData.title,
            imgUrl = offerData.imgUrl,
            gasStation = gs
        )

        offerRepository.save(newOffer)

        return newOffer
    }

    @Throws(
        GasStationIsNotFoundException::class, GasStationDoesNotBelongToManagerException::class,
        OfferIsNotFoundException::class)
    override fun deleteOffer(gs_id: Long, offer_id: Long, manager: User): Offer {
        val gs = gasStationRepository.findById(gs_id).orElseThrow{ GasStationIsNotFoundException() }
        if(!gs.manager!!.equals(manager)) throw GasStationDoesNotBelongToManagerException()

        val offer = offerRepository.findByIdAndGasStationId(offer_id, gs_id).orElseThrow{ OfferIsNotFoundException() }

        offer.isDeleted = true

        offerRepository.save(offer)

        return offer
    }

    @Throws(
        GasStationIsNotFoundException::class, GasStationDoesNotBelongToManagerException::class,
        OfferIsNotFoundException::class)
    override fun changeSchedule(gs_id: Long, schedule: ScheduleRequest, manager: User): GasStation {
        val gs = gasStationRepository.findById(gs_id).orElseThrow{ GasStationIsNotFoundException() }
        if(!gs.manager!!.equals(manager)) throw GasStationDoesNotBelongToManagerException()

        val sc = scheduleRepository.findScheduleByDayOfWeekAndGasStationId(schedule.dayOfWeek, gs_id).orElseThrow{Exception()}

        sc.isClosed = schedule.isClosed
        sc.isAroundTheClock = schedule.isAroundTheClock
        sc.openFrom = schedule.openFrom
        sc.openTo = schedule.openTo

        scheduleRepository.save(sc)

        return gs

    }

    @Throws(
        GasStationIsNotFoundException::class, GasStationDoesNotBelongToManagerException::class,
        GasStationServicesIsEmptyException::class)
    override fun updateServicesList(gs_id: Long, services: String, manager: User): GasStation {
        val gs = gasStationRepository.findById(gs_id).orElseThrow{ GasStationIsNotFoundException() }
        if(!gs.manager!!.equals(manager)) throw GasStationDoesNotBelongToManagerException()
        if(services.isEmpty()) throw GasStationServicesIsEmptyException()

        gs.servicesListString = services

        gasStationRepository.save(gs)

        return gs
    }

    @Throws(
        GasStationIsNotFoundException::class, GasStationDoesNotBelongToManagerException::class,
        GasStationPaymentMethodsIsEmptyException::class)
    override fun updatePaymentMethodsList(gs_id: Long, pM: String, manager: User): GasStation {
        val gs = gasStationRepository.findById(gs_id).orElseThrow{ GasStationIsNotFoundException() }
        if(!gs.manager!!.equals(manager)) throw GasStationDoesNotBelongToManagerException()

        if(pM.isEmpty()) throw GasStationPaymentMethodsIsEmptyException()

        gs.paymentMethodsListString = pM

        gasStationRepository.save(gs)

        return gs

    }

    @Throws(
        GasStationIsNotFoundException::class, GasStationDoesNotBelongToManagerException::class,
        RefuelingStandAlreadyExistException::class)
    override fun addRefuelingStand(gs_id: Long, stand_id: Int, manager: User): RefuelingStand {
        val gs = gasStationRepository.findById(gs_id).orElseThrow{ GasStationIsNotFoundException() }
        if(!gs.manager!!.equals(manager)) throw GasStationDoesNotBelongToManagerException()

        if(refuelingStandRepository.findRefuelingStandByStandIdAndGasStationId(stand_id, gs.id).isPresent) throw RefuelingStandAlreadyExistException()

        val rS = RefuelingStand(
            standId = stand_id,
            gasStation = gs
        )

        refuelingStandRepository.save(rS)

        return rS

    }

    @Throws(
        GasStationIsNotFoundException::class, GasStationDoesNotBelongToManagerException::class,
        FuelIsNotFoundException::class)
    override fun addFuelToRefuelingStand(gs_id: Long, data: ConnectRefStandAndFuelRequest, manager: User): RefuelingStand {
        val gs = gasStationRepository.findById(gs_id).orElseThrow{ GasStationIsNotFoundException() }
        if(!gs.manager!!.equals(manager)) throw GasStationDoesNotBelongToManagerException()

        val fuel = fuelRepository.findFuelById(data.fuel_id).orElseThrow{ FuelIsNotFoundException("Fuel with id - ${data.fuel_id} was not found!") }
        val r_stands = refuelingStandRepository.findAllByGasStationId(gs_id)
        println(fuel.toString())
        val stand = r_stands.first{it.standId == data.stand_id}
//        if(stand) throw cz.cvut.fel.mygs.exceptios.FuelIsNotFoundException("Refueling stand was not found! " +
//                "Paramenters - {gs_id - ${gs_id}, rs_id - ${data.stand_id}} ")

        println(fuelRepository.findAllByRsContainsRefuelingStand(data.stand_id).size)

        stand.fuels.add(fuel)

        refuelingStandRepository.save(stand)

        return stand
    }

    @Throws(GasStationIsNotFoundException::class, GasStationDoesNotBelongToManagerException::class)
    override fun addFuelToGasStation(gs_id: Long, fuelData: AddNewFuelRequest, manager: User): Fuel {
        val gs = gasStationRepository.findById(gs_id).orElseThrow{ GasStationIsNotFoundException() }
        if(!gs.manager!!.equals(manager)) throw GasStationDoesNotBelongToManagerException()

        val fuel = Fuel(
            name = fuelData.name,
            price = fuelData.price,
            gasStation = gs,
            fuelType = fuelTypeFilterToEnum(fuelData.fuelType)
        )

        fuelRepository.save(fuel)

        return fuel
    }

    @Throws(
        GasStationIsNotFoundException::class, GasStationDoesNotBelongToManagerException::class,
        FuelIsNotFoundException::class)
    override fun updateFuelPrice(gs_id: Long, fuelData: UpdateFuelPriceRequest, manager: User): Fuel {
        val gs = gasStationRepository.findById(gs_id).orElseThrow{ GasStationIsNotFoundException() }
        if(!gs.manager!!.equals(manager)) throw GasStationDoesNotBelongToManagerException()

        val fuel = fuelRepository.findFuelByIdAndGasStationId(fuelData.fuel_id, gs_id).orElseThrow{ FuelIsNotFoundException() }
        fuel.price = fuelData.price

        fuelRepository.save(fuel)
        return fuel
    }

}