package cz.cvut.fel.mygs.repositories

import cz.cvut.fel.mygs.models.Car
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CarRepository: JpaRepository<Car, Long> {

    override fun findAll(): List<Car>
    fun findCarById(id: Long): Optional<Car>

    fun findCarsByOwnerId(userId: Long): List<Car>
    fun deleteCarById(id: Long): Car

}