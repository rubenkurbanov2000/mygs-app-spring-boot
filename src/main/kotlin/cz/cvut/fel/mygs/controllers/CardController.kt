package cz.cvut.fel.mygs.controllers

import cz.cvut.fel.mygs.assemblers.CarAssembler
import cz.cvut.fel.mygs.assemblers.CardAssembler
import cz.cvut.fel.mygs.models.Car
import cz.cvut.fel.mygs.models.Card
import cz.cvut.fel.mygs.repositories.CardRepository
import cz.cvut.fel.mygs.repositories.UserRepository
import cz.cvut.fel.mygs.services.CardService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/card")
class CardController(
    val cardRepository: CardRepository,
    val cardService: CardService,
    val userRepository: UserRepository,
    val cardAssembler: CardAssembler
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(CardController::class.java)
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/")
    fun getAllCards(fromUser: HttpServletRequest): ResponseEntity<List<CardAssembler.CardDto>> {

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)
        LOG.info(
            "REQUEST /card/: Get list of cards from user - ${user.email}"
        )

        val cards: List<Card> =  cardService.getAllCards(user.email)

        val response = cardAssembler.toListCardDto(cards)

        LOG.info(
            "RESPONSE /card/: Get list of cards from user - ${user.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/")
    fun addNewCard(@RequestBody cardData: NewAddNewCardRequest,
                  fromUser: HttpServletRequest
    ): ResponseEntity<CardAssembler.CardDto> {

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)
        LOG.info(
            "REQUEST /card/: Add new card for user - ${user.email}"
        )

        val card : Card = cardService.addNewCard(cardData, user)
        val response = cardAssembler.toCardDto(card)
        LOG.info(
            "REQUEST /card/: Add new car for user - ${user.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{card-id}")
    fun deleteCard(@PathVariable(name = "card-id") cardId: Long,
                  fromUser: HttpServletRequest
    ): ResponseEntity<CardAssembler.CardDto> {

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)
        LOG.info(
            "REQUEST /card/{card-id}: Delete card with id - $cardId " +
                    "from user - ${user.email}"
        )

        val card : Card = cardService.deleteCard(cardId, user)
        val response = cardAssembler.toCardDto(card)
        LOG.info(
            "REQUEST /card/{card-id}: Delete card with id - $cardId" +
                    " from user - ${user.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }
}

data class NewAddNewCardRequest(
    val cardNumber: String,
    val dateExp: LocalDateTime,
    val cvv: Int
)

