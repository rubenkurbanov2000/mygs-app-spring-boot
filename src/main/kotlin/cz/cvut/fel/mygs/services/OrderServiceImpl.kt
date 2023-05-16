package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.exceptios.CardIsNotFoundExceptions
import cz.cvut.fel.mygs.exceptios.FuelIsNotFoundException
import cz.cvut.fel.mygs.exceptios.GasStationIsNotFoundException
import cz.cvut.fel.mygs.exceptios.WrongAmountOfFillingUnitException
import cz.cvut.fel.mygs.controllers.AddNewOrderRequest
import cz.cvut.fel.mygs.controllers.FilterOrdersRequest
import cz.cvut.fel.mygs.models.Order
import cz.cvut.fel.mygs.models.User
import cz.cvut.fel.mygs.repositories.*
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.jvm.Throws

@Service
class OrderServiceImpl(
    val orderRepository: OrderRepository,
    val userRepository: UserRepository,
    val gasStationRepository: GasStationRepository,
    val fuelRepository: FuelRepository,
    val carRepository: CarRepository,
    val cardRepository: CardRepository

): OrderService {
    override fun getAllOrders(user: User): List<Order> {
        return orderRepository.findAllByClientId(user.id)
    }

    @Throws(
        WrongAmountOfFillingUnitException::class, CardIsNotFoundExceptions::class,
        GasStationIsNotFoundException::class, FuelIsNotFoundException::class)
    override fun createOrder(order: AddNewOrderRequest, user: User): Order {
        if((order.countLitters == 0.0) or
            (order.totalAmount == 0.0)) throw WrongAmountOfFillingUnitException()

        val car = carRepository.findCarsByOwnerId(user.id).lastOrNull { it.id == order.refueledCarId }
        val carNumber: String = car?.number ?: "No selected"

        val card = cardRepository.findCardsByOwnerId(user.id).lastOrNull { it.id == order.refueledCardId }
            ?: throw CardIsNotFoundExceptions()

        val gasStation = gasStationRepository.findById(order.gasStationId).orElseThrow{ GasStationIsNotFoundException() }

        val fuel = fuelRepository.findById(order.fuelId).orElseThrow{ FuelIsNotFoundException() }

        val order = Order(
            countLitters = order.countLitters,
            totalAmount = order.totalAmount,
            created = LocalDateTime.now(),
            refueledCardNumber = card.lastFourDigits,
            refueledCarNumber = carNumber,
            client = user,
            gasStation = gasStation,
            fuel = fuel
        )

        orderRepository.save(order)
        return order

    }

    override fun getFilterOrdersList(filterProperties: FilterOrdersRequest, user: User): List<Order> {
        val allOrders: List<Order> = getAllOrders(user)
        val filteredList: MutableList<Order> = allOrders.toMutableList()

        if(filterProperties.date != null)
            filteredList.removeAll{it.created.dayOfYear != filterProperties.date!!.dayOfYear}
        if (filterProperties.priceFrom != null)
            filteredList.removeAll{it.totalAmount < filterProperties.priceFrom!!}
        if(filterProperties.priceTo != null)
            filteredList.removeAll{it.totalAmount > filterProperties.priceTo!!}
        if(filterProperties.carNumber != null)
            filteredList.removeAll{it.refueledCarNumber != filterProperties.carNumber}
        if(filterProperties.cardNumber != null)
            filteredList.removeAll{it.refueledCardNumber != filterProperties.cardNumber}

        return filteredList

    }
}