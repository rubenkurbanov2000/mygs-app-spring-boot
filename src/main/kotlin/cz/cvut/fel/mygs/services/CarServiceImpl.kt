package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.exceptios.CarIsNotFoundExceptions
import cz.cvut.fel.mygs.exceptios.EmptyCarBrandNameExceptions
import cz.cvut.fel.mygs.exceptios.EmptyCarNumberExceptions
import cz.cvut.fel.mygs.exceptios.UnknownFuelTypeExceptions
import cz.cvut.fel.mygs.exceptios.UserIsNotFoundException
import cz.cvut.fel.mygs.controllers.CarAddNewRequest
import cz.cvut.fel.mygs.models.Car
import cz.cvut.fel.mygs.models.FuelType
import cz.cvut.fel.mygs.models.User
import cz.cvut.fel.mygs.repositories.CarRepository
import cz.cvut.fel.mygs.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CarServiceImpl(
    val carRepository: CarRepository,
    val userRepository: UserRepository
) : CarService {

    @Throws(UserIsNotFoundException::class)
    override fun getAllCars(userEmail: String): List<Car> {

        val user: User = userRepository.findByEmail(userEmail)
            .orElseThrow { UserIsNotFoundException("User with email $userEmail is not registered!") }

        val cars = carRepository.findCarsByOwnerId(user.id).filter{ !it.deleted }

        return cars
    }

    override fun getCarById(id: Long): Car {
        TODO("Not yet implemented")
    }

    @Throws(
        EmptyCarNumberExceptions::class, UnknownFuelTypeExceptions::class,
        EmptyCarBrandNameExceptions::class)
    override fun addNewCar(carData: CarAddNewRequest, user: User): Car {

        if(carData.brand.isEmpty()) throw EmptyCarBrandNameExceptions()
        if(carData.number.isEmpty()) throw EmptyCarNumberExceptions()
        if(!enumContains(fuelTypeFilterToString(carData.fuelType))) throw UnknownFuelTypeExceptions()

        val car = Car(
            brand = carData.brand,
            number = carData.number,
            fuelType = fuelTypeFilterToEnum(carData.fuelType),
            owner = user
        )

        carRepository.save(car)
        return car
    }
    @Throws(CarIsNotFoundExceptions::class)
    @Transactional
    override fun deleteCar(carId: Long, user: User):Car {
        if(!carRepository.findCarsByOwnerId(user.id).any{it.id == carId}) throw CarIsNotFoundExceptions()

        val car = carRepository.findCarById(carId).orElse(null)

        car.deleted = true
        carRepository.save(car)
        return car
    }


    /**
     * Returns `true` if enum FuelType contains an entry with the specified name.
     *
     * copied from https://stackoverflow.com/questions/41844080/kotlin-how-to-check-if-enum-contains-a-given-string-without-messing-with-except
     */
     fun enumContains(name: String): Boolean {
        return enumValues<FuelType>().any { it.name == name}
    }

    fun fuelTypeFilterToString(id: Int): String{

        if (id == 0) return "DIESEL"
        if (id == 1) return "PETROL"
        if(id == 2) return "PROPANE"
        return ""

    }
    fun fuelTypeFilterToEnum(id: Int): FuelType{
        if (id == 0) return FuelType.DIESEL
        if (id == 1) return FuelType.PETROL
        return FuelType.PROPANE
    }
}