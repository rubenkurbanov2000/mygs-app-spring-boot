package cz.cvut.fel.mygs.assemblers

import cz.cvut.fel.mygs.models.Card
import cz.cvut.fel.mygs.models.Order
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OrderAssembler {

    fun toOrderDto(order: Order): OrderAssembler.OrderDto {
        return OrderAssembler.OrderDto(

            order.id,
            order.countLitters,
            order.created,
            order.totalAmount,
            order.refueledCarNumber,
            order.refueledCardNumber,
            order.gasStation!!.name,
            order.gasStation!!.getAddres(),
            order.fuel!!.name
        )
    }

    fun toListOrderDto(orderList: List<Order>): List<OrderAssembler.OrderDto>{
        return if(orderList.isNotEmpty()){
            orderList.map { toOrderDto(it) }
        }else{
            listOf()
        }
    }
    data class OrderDto(val id: Long,
                        val countLitters: Double,
                        val created: LocalDateTime,
                        val totalAmount: Double,
                        val refueledCarNumber: String,
                        val refueledCardNumber: String,
                        val gasStationName: String,
                        val gasStationAddress: String,
                        val fuelName: String,
    ) {
        override fun toString(): String {
            return "OrderDto(id=$id, countLitters=$countLitters, created=$created, totalAmount=$totalAmount, refueledCarNumber='$refueledCarNumber', refueledCardNumber='$refueledCardNumber', gasStationName='$gasStationName', gasStationAddress='$gasStationAddress', fuelName='$fuelName')"
        }
    }


}
