package cz.cvut.fel.mygs.assemblers

import cz.cvut.fel.mygs.models.Fuel
import cz.cvut.fel.mygs.models.FuelType
import cz.cvut.fel.mygs.models.RefuelingStand
import cz.cvut.fel.mygs.repositories.FuelRepository
import org.springframework.stereotype.Component

@Component
class RefuelingStandAssembler(
    val fuelAssembler: FuelAssembler,
    val fuelRepository: FuelRepository
) {

    fun toRefuelingStandDto(rs: RefuelingStand): RefuelingStandAssembler.RefuelingStandDto {
        return  RefuelingStandAssembler.RefuelingStandDto(
            rs.id,
            rs.standId,
            fuelAssembler.toListFuelDto(fuelRepository.findAllByRsContainsRefuelingStand(rs.standId))
        )
    }

    fun toListRefuelingStandDto(rsList: List<RefuelingStand>): List<RefuelingStandAssembler.RefuelingStandDto>{
        return if(rsList.isNotEmpty()){
            rsList.map { toRefuelingStandDto(it) }
        }else{
            listOf()
        }
    }
    data class RefuelingStandDto(
        val id: Long,
        val standId: Int,
        val fuels: List<FuelAssembler.FuelDto>
    )


}

