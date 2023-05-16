package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.controllers.AddNewOrderRequest
import cz.cvut.fel.mygs.controllers.FilterOrdersRequest
import cz.cvut.fel.mygs.models.Order
import cz.cvut.fel.mygs.models.User

interface OrderService {

    fun getAllOrders(user: User): List<Order>
    fun createOrder(order: AddNewOrderRequest, user: User): Order

    fun getFilterOrdersList(filterProperties: FilterOrdersRequest, user: User): List<Order>

}