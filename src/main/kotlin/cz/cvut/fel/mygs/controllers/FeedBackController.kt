package cz.cvut.fel.mygs.controllers

import cz.cvut.fel.mygs.assemblers.FeedBackAssembler
import cz.cvut.fel.mygs.assemblers.GasStationAssembler
import cz.cvut.fel.mygs.repositories.FeedBackRepository
import cz.cvut.fel.mygs.repositories.UserRepository
import cz.cvut.fel.mygs.services.FeedBackService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("/feedback")
class FeedBackController(
    val feedBackAssembler: FeedBackAssembler,
    val userRepository: UserRepository,
    val feedBackRepository: FeedBackRepository,
    val feedBackService: FeedBackService
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(FeedBackController::class.java)
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/gs/{gs_id}")
    fun getFeedBackFromUserToGasStationIfExist(@PathVariable gs_id: Long, fromUser: HttpServletRequest): ResponseEntity<FeedBackAssembler.FeedBackDto?> {

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /feedback/gs/{gs_id}: Checking if the user ${user.email} has already left a review about" +
                    " the gas station with ID - $gs_id"
        )
        val feedback = feedBackService.getFeedBackFromUserToGasStationIfExist(gs_id, user)
        var response: FeedBackAssembler.FeedBackDto? = null
        if (feedback != null) response = feedBackAssembler.toFeedBackDto(feedback)

        LOG.info(
            "RESPONSE /feedback/gs/{gs_id}: Checking if the user ${user.email} has already left a review about" +
                    " the gas station with ID - $gs_id. Response - ${response.toString()}"
        )

        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/order/{order_id}")
    fun getFeedBackFromUserToOrderIfExist(@PathVariable order_id: Long, fromUser: HttpServletRequest): ResponseEntity<FeedBackAssembler.FeedBackDto?> {
        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /feedback/order/{order_id}: Checking if the user ${user.email} has already left a review about" +
                    " the order with ID - $order_id"
        )
        val feedback = feedBackService.getFeedBackFromUserToOrderIfExist(order_id, user)
        var response: FeedBackAssembler.FeedBackDto? = null
        if (feedback != null) response = feedBackAssembler.toFeedBackDto(feedback)

        LOG.info(
            "RESPONSE /feedback/order/{order_id}: Checking if the user ${user.email} has already left a review about" +
                    " the order with ID - $order_id. Response - ${response.toString()}"
        )

        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/order/{order_id}")
    fun leaveFeedbackAboutOrder(@PathVariable order_id: Long,fromUser: HttpServletRequest, @RequestBody leaveFeedbackRequest: LeaveFeedbackRequest): ResponseEntity<FeedBackAssembler.FeedBackDto> {
        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /feedback/order/{order_id}: Create or edit a feedback about the order with id - $order_id, by user - ${user.email}"
        )

        val feedback = feedBackService.leaveFeedbackAboutOrder(order_id, user, leaveFeedbackRequest)
        val response = feedBackAssembler.toFeedBackDto(feedback)

        LOG.info(
            "RESPONSE /feedback/order/{order_id}: Create or edit a feedback about the order with id - $order_id, by user - ${user.email} SUCCESS"
        )

        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/gs/{gs_id}")
    fun leaveFeedbackAboutGS(@PathVariable gs_id: Long, fromUser: HttpServletRequest, @RequestBody leaveFeedbackRequest: LeaveFeedbackRequest): ResponseEntity<FeedBackAssembler.FeedBackDto> {
        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)

        LOG.info(
            "REQUEST /feedback/gs/{gs_id}: Create or edit a feedback about the gas station with id - $gs_id, by user - ${user.email}"
        )

        val feedback = feedBackService.leaveFeedbackAboutGS(gs_id, user, leaveFeedbackRequest)
        val response = feedBackAssembler.toFeedBackDto(feedback)

        LOG.info(
            "RESPONSE /feedback/gs/{gs_id}: Create or edit a feedback about the gas station with id - $gs_id, by user - ${user.email} SUCCESS"
        )

        return ResponseEntity.ok(response)
    }

}

data class LeaveFeedbackRequest(
    val content: String,
    val title: String,
    val rating: Int
)