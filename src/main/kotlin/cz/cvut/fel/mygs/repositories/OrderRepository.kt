package cz.cvut.fel.mygs.repositories

import cz.cvut.fel.mygs.models.Car
import cz.cvut.fel.mygs.models.Order
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OrderRepository: JpaRepository<Order, Long> {

    fun findAllByClientId(clientId: Long): List<Order>

}