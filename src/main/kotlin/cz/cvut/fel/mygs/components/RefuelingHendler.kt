package cz.cvut.fel.mygs.components

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import cz.cvut.fel.mygs.controllers.AddNewOrderRequest
import cz.cvut.fel.mygs.controllers.CarController
import cz.cvut.fel.mygs.models.User
import cz.cvut.fel.mygs.repositories.CarRepository
import cz.cvut.fel.mygs.repositories.CardRepository
import cz.cvut.fel.mygs.repositories.FuelRepository
import cz.cvut.fel.mygs.repositories.UserRepository
import cz.cvut.fel.mygs.services.BankService
import cz.cvut.fel.mygs.services.OrderService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.HashMap
import kotlin.concurrent.schedule
import kotlin.math.roundToInt

class Message(
    val filled_price: Double,
    val filled_liters: Int,
    val state: String,
    val is_staff_waiting: Boolean
)

class RefuelingHandler(
    val userRepository: UserRepository,
    val orderService: OrderService,
    val bankService: BankService,
    val fuelRepository: FuelRepository
): TextWebSocketHandler() {

    companion object {
        private val LOG = LoggerFactory.getLogger(RefuelingHandler::class.java)
    }


    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        LOG.info("Connection closed by client")
    }

    public override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {

        val json = ObjectMapper().readTree(message.payload)

        val userId = json.get("user_id").asLong()
        val countLitters = json.get("count_litters").asDouble().roundToInt()
        val totalAmount = json.get("total_amount").asDouble()
        val refueledCarId = json.get("refueledCar_id").asLong()
        val refueledCardId = json.get("refueledCard_id").asLong()
        val gasStationId = json.get("gasStation_id").asLong()
        val fuelId = json.get("fuel_id").asLong()
        val isStaff = json.get("is_staff_need").asBoolean()

        var readyLitters = 0
        var readyPrice = 0.0

        val user = userRepository.findById(userId).orElse(null) ?: return
        val fuel = fuelRepository.findById(fuelId).orElse(null)?: return

        emit(session, Message(readyPrice, readyLitters,"Initialized", isStaff))
        bankService.payForRefueling(totalAmount)?:return

        val order =  AddNewOrderRequest(
            countLitters.toDouble(),
            totalAmount,
            refueledCarId,
            refueledCardId,
            gasStationId,
            fuelId
        )
        orderService.createOrder( order, user)

        if(isStaff){
            emit(session, Message(readyPrice, readyLitters,"WaitingStaff", isStaff))
            Timer().schedule(10000) {
                emit(session, Message(readyPrice, readyLitters,"Process", isStaff))

                while (readyLitters != countLitters){

                    readyPrice = readyLitters*fuel.price

                    Timer().schedule(500){
                        emit(session, Message(readyPrice, readyLitters,"Process", isStaff))
                        readyLitters += 1
                    }
                }
            }
        }else{
            emit(session, Message(readyPrice, readyLitters,"Waiting", isStaff))
            Timer().schedule(3000) {
                emit(session, Message(readyPrice, readyLitters,"Process", isStaff))

                while (readyLitters != countLitters){

                    readyPrice = readyLitters*fuel.price

                    Timer().schedule(500){
                        emit(session, Message(readyPrice, readyLitters,"Process", isStaff))
                        readyLitters += 1
                    }
                }

            }
        }
        session.close()


        /**
         * REQUEST JSON Data:
         *
         * user_id: Long,
         * count_litters: Double,
         * total_amount: Double,
         * refueledCar_id: Long,
         * refueledCard_id: Long,
         * gasStation_id: Long,
         * fuel_id: Long,
         * is_staff_need: Boolean
         *
         * -----------------
         *
         * RESPONSE JSON Data
         *
         * fuel_price: Double
         * filled_liters: Double
         * state: String
         * is_staff_waiting: Boolean
         *
         */

    }

    fun emit(session: WebSocketSession, msg: Message) = session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(msg)))
}

@Configuration
@EnableWebSocket
class WebSocketConfig(
    val userRepository: UserRepository,
    val orderService: OrderService,
    val bankService: BankService,
    val fuelRepository: FuelRepository

) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(RefuelingHandler(userRepository, orderService, bankService, fuelRepository), "/refueling").addInterceptors(WebSocketHandshakeAuthInterceptor(userRepository))
    }

}

@Component
class WebSocketHandshakeAuthInterceptor(
    val userRepository: UserRepository
): HandshakeInterceptor {

    companion object {
        private val LOG = LoggerFactory.getLogger(WebSocketHandshakeAuthInterceptor::class.java)
    }

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        println(request.principal)
        val user = userRepository.findByEmail(request.principal!!.name).orElse(null)
        return if(user != null){
            LOG.info("The service is successfully connected to the Web socket and is ready to transmit data. " +
                    "Active user - ${user.email}")
            true
        }else{
            LOG.info("The service is not connected to the Web socket! Unauthorized connection!")
            false
        }
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: java.lang.Exception?
    ) {
        println("IS OKE")
        return
    }

}
