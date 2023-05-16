package cz.cvut.fel.mygs.controllers

import cz.cvut.fel.mygs.assemblers.CardAssembler
import cz.cvut.fel.mygs.assemblers.OrderAssembler
import cz.cvut.fel.mygs.models.Card
import cz.cvut.fel.mygs.models.GasStation
import cz.cvut.fel.mygs.models.Order
import cz.cvut.fel.mygs.models.User
import cz.cvut.fel.mygs.repositories.OrderRepository
import cz.cvut.fel.mygs.repositories.UserRepository
import cz.cvut.fel.mygs.services.OrderService
import cz.cvut.fel.mygs.services.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/order")
class OrderController(
    val orderService: OrderService,
    val orderRepository: OrderRepository,
    val userService: UserService,
    val orderAssembler: OrderAssembler,
    val userRepository: UserRepository
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(OrderController::class.java)
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/")
    fun getAllOrders(fromUser: HttpServletRequest): ResponseEntity<List<OrderAssembler.OrderDto>> {

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /order/: Get list of orders from user - ${user.email}"
        )

        val orders: List<Order> =  orderService.getAllOrders(user)
        val response = orderAssembler.toListOrderDto(orders)

        LOG.info(
            "RESPONSE /order/: Get list of orders from user - ${user.email} " +
                    "with response - ${response.toString()}"
        )
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/filter")
    fun getFilteredOrderList(@RequestBody filterOrdersRequest: FilterOrdersRequest, fromUser: HttpServletRequest): ResponseEntity<List<OrderAssembler.OrderDto>> {

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /order/filter/: Get filtered list of orders from user - ${user.email} " +
                    "with properties ${filterOrdersRequest.toString()}"
        )

        val orders: List<Order> =  orderService.getFilterOrdersList(filterOrdersRequest, user)
        val response = orderAssembler.toListOrderDto(orders)

        LOG.info(
            "RESPONSE /order/filter/: Get filtered list of orders from user - ${user.email} " +
                    "with response - ${response.toString()}"
        )
        return ResponseEntity.ok(response)
    }

}

data class AddNewOrderRequest(
    val countLitters: Double,
    val totalAmount: Double,
    val refueledCarId: Long,
    val refueledCardId: Long,
    val gasStationId: Long,
    val fuelId: Long,
)

data class FilterOrdersRequest(
    val date: LocalDateTime?,
    val priceFrom: Double?,
    val priceTo: Double?,
    val carNumber: String?,
    val cardNumber: String?,
) {
    override fun toString(): String {
        return "FilterOrdersRequest(date=$date, priceFrom=$priceFrom, priceTo=$priceTo, carNumber=$carNumber, cardNumber=$cardNumber)"
    }
}