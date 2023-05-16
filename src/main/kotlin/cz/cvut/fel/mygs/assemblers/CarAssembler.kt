package cz.cvut.fel.mygs.assemblers

import cz.cvut.fel.mygs.models.Car
import cz.cvut.fel.mygs.models.FuelType
import cz.cvut.fel.mygs.models.Gender
import cz.cvut.fel.mygs.models.User
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CarAssembler {

    fun toCarDto(car: Car): CarAssembler.CarDto {
        return CarAssembler.CarDto(

            car.id,
            car.brand,
            car.number,
            car.fuelTypeFilterToId()
        )
    }

    fun toListCarDto(carList: List<Car>): List<CarAssembler.CarDto>{
        return if(carList.isNotEmpty()){
            carList.map { toCarDto(it) }
        }else{
            listOf()
        }
    }
    data class CarDto(val id: Long,
                       val brand: String,
                       val number: String,
                       val fuelType: Int,
                       )


}

