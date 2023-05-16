package cz.cvut.fel.mygs.assemblers

import cz.cvut.fel.mygs.models.Card
import cz.cvut.fel.mygs.models.Fuel
import cz.cvut.fel.mygs.models.FuelType
import org.springframework.stereotype.Component

@Component
class FuelAssembler {

    fun toFuelDto(fuel: Fuel): FuelAssembler.FuelDto {
        return FuelAssembler.FuelDto(
            fuel.id,
            fuel.name,
            fuel.price,
            fuel.fuelType
        )
    }

    fun toListFuelDto(fuelList: List<Fuel>): List<FuelAssembler.FuelDto>{
        return if(fuelList.isNotEmpty()){
            fuelList.map { toFuelDto(it) }
        }else{
            listOf()
        }
    }
    data class FuelDto(
        val id: Long,
        val name: String,
        val price: Double,
        val fuelType: FuelType
    )


}

