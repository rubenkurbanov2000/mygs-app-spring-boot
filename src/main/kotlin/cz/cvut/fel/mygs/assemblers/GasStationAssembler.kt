package cz.cvut.fel.mygs.assemblers

import cz.cvut.fel.mygs.controllers.FullGasStationInfo
import cz.cvut.fel.mygs.controllers.ShortGasStationInfo
import cz.cvut.fel.mygs.models.Fuel
import cz.cvut.fel.mygs.models.GasStation
import cz.cvut.fel.mygs.models.Order
import cz.cvut.fel.mygs.models.RefuelingStand
import cz.cvut.fel.mygs.repositories.GasStationRepository
import org.springframework.stereotype.Component
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime

@Component
class GasStationAssembler(
    val gasStationRepository: GasStationRepository,
    val refuelingStandAssembler: RefuelingStandAssembler,
    val fuelAssembler: FuelAssembler
) {

    fun toShortGsDto(gsData: ShortGasStationInfo): GasStationAssembler.ShortGSDto {
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.UP

        val wh = gsData.gs.schedules!!.first { it.dayOfWeek == LocalDateTime.now().dayOfWeek.value }
        println(wh.openFrom)

        return GasStationAssembler.ShortGSDto(
            gsData.gs.id,
            gsData.gs.getAddres(),
            gsData.gs.name,
            df.format(gsData.distance).toDouble(),
            wh.openFrom,
            wh.openTo,
            wh.isClosed,
            wh.isAroundTheClock,
            gsData.isFavorite,
            df.format(gsData.gs.rating).toDouble(),
        )
    }

    fun toFullGsDto(gsData: FullGasStationInfo): GasStationAssembler.FullGSDto {
        val df = DecimalFormat("#.#")
        val wh = gsData.gs.schedules!!.first { it.dayOfWeek == LocalDateTime.now().dayOfWeek.value }

        return GasStationAssembler.FullGSDto(
            gsData.gs.id,
            gsData.gs.getAddres(),
            gsData.gs.name,
            gsData.distance,
            wh.openFrom,
            wh.openTo,
            wh.isClosed,
            wh.isAroundTheClock,
            gsData.isFavorite,
            fuelAssembler.toListFuelDto(gsData.priceList),
            gsData.gs.getPaymentMethods(),
            gsData.gs.getServices(),
            refuelingStandAssembler.toListRefuelingStandDto(gsData.refuelingStands),
            df.format(gsData.gs.rating).toDouble(),
        )
    }

    fun toListShortGsDto(gsData: List<ShortGasStationInfo>): List<GasStationAssembler.ShortGSDto>{
        return if(gsData.isNotEmpty()){
            gsData.map { toShortGsDto(it) }
        }else{
            listOf()
        }
    }

    fun toListFullGsDto(gsData: List<FullGasStationInfo>): List<GasStationAssembler.FullGSDto>{
        return if(gsData.isNotEmpty()){
            gsData.map { toFullGsDto(it) }
        }else{
            listOf()
        }
    }

    data class FullGSDto(
        val id: Long,
        val address: String,
        val firmName: String,
        val distance: Double,
        val openFrom: LocalDateTime?,
        val openTo: LocalDateTime?,
        val isClosed: Boolean,
        val isAroundTheClock: Boolean,
        val isFavorite: Boolean,
        val priceList: List<FuelAssembler.FuelDto>,
        val paymentMethods: List<Int>,
        val services: List<Int>,
        val refuelingStands: List<RefuelingStandAssembler.RefuelingStandDto>,
        val rating: Double
    ) {
        override fun toString(): String {
            return "FullGSDto(id=$id, address='$address', firmName='$firmName', distance=$distance, openFrom=$openFrom, openTo=$openTo, isFavorite=$isFavorite, priceList=$priceList, paymentMethods=$paymentMethods, services=$services, refuelingStands=$refuelingStands)"
        }

    }

    data class ShortGSDto(
        val id: Long,
        val address: String,
        val firmName: String,
        val distance: Double,
        val openFrom: LocalDateTime?,
        val openTo: LocalDateTime?,
        val isClosed: Boolean,
        val isAroundTheClock: Boolean,
        val isFavorite: Boolean,
        val rating: Double
    ) {
        override fun toString(): String {
            return "ShortGSDto(id=$id, address='$address', firmName='$firmName', distance=$distance, openFrom=$openFrom, openTo=$openTo, isFavorite=$isFavorite)"
        }

    }


}
