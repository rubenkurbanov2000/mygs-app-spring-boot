package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.controllers.CarAddNewRequest
import cz.cvut.fel.mygs.models.Car
import cz.cvut.fel.mygs.models.User

interface CarService {

    fun getAllCars(userEmail: String): List<Car>
    fun getCarById(id: Long): Car
    fun addNewCar(carData: CarAddNewRequest, user: User): Car
    fun deleteCar(id: Long, user: User): Car

}