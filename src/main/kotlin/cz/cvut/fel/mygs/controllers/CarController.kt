package cz.cvut.fel.mygs.controllers

import cz.cvut.fel.mygs.assemblers.CarAssembler
import cz.cvut.fel.mygs.models.Car
import cz.cvut.fel.mygs.models.Question
import cz.cvut.fel.mygs.repositories.CarRepository
import cz.cvut.fel.mygs.repositories.UserRepository
import cz.cvut.fel.mygs.services.CarService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/car")
class CarController(
    val userRepository: UserRepository,
    val carRepository: CarRepository,
    val carService: CarService,
    val carAssembler: CarAssembler
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(CarController::class.java)
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/")
    fun getAllCars(fromUser: HttpServletRequest): ResponseEntity<List<CarAssembler.CarDto>> {

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)
        LOG.info(
            "REQUEST /car/: Get list of cars from user - ${user.email}"
        )

        val cars: List<Car> =  carService.getAllCars(user.email)
        val response = carAssembler.toListCarDto(cars)

        LOG.info(
            "RESPONSE /car/: Get list of cars from user - ${user.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/")
    fun addNewCar(@RequestBody carData: CarAddNewRequest,
                  fromUser: HttpServletRequest): ResponseEntity<CarAssembler.CarDto> {

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)
        LOG.info(
            "REQUEST /car/: Add new car ${carData.brand} with " +
                    "number ${carData.number}" +
                    " for user - ${user.email}"
        )

        val car : Car = carService.addNewCar(carData, user)
        val response = carAssembler.toCarDto(car)

        LOG.info(
            "REQUEST /car/: Add new car ${carData.brand} with " +
                    "number ${carData.number}" +
                    " for user - ${user.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{car-id}")
    fun deleteCar(@PathVariable(name = "car-id") carId: Long,
                  fromUser: HttpServletRequest): ResponseEntity<CarAssembler.CarDto> {

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)
        LOG.info(
            "REQUEST /car/{car-id}: Delete car with id - $carId" +
                    " from user - ${user.email}"
        )

        val car : Car = carService.deleteCar(carId, user)
        val response = carAssembler.toCarDto(car)

        LOG.info(
            "REQUEST /car/{car-id}: Delete car with id - $carId" +
                    " from user - ${user.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }
}

data class CarAddNewRequest(
    val brand: String,
    val number: String,
    val fuelType: Int
)
