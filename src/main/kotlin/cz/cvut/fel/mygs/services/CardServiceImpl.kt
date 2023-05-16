package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.exceptios.CardExpiredException
import cz.cvut.fel.mygs.exceptios.CardIsNotFoundExceptions
import cz.cvut.fel.mygs.exceptios.EmptyCardNumberExceptions
import cz.cvut.fel.mygs.exceptios.FailedToRegisterCardException
import cz.cvut.fel.mygs.exceptios.UserIsNotFoundException
import cz.cvut.fel.mygs.exceptios.WrongCardNumberFormatExceptions
import cz.cvut.fel.mygs.exceptios.WrongCvvCodeExceptions
import cz.cvut.fel.mygs.controllers.NewAddNewCardRequest
import cz.cvut.fel.mygs.models.Card
import cz.cvut.fel.mygs.models.User
import cz.cvut.fel.mygs.repositories.CardRepository
import cz.cvut.fel.mygs.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

import kotlin.jvm.Throws

@Service
class CardServiceImpl(
    val cardRepository: CardRepository,
    val userRepository: UserRepository,
    val bankService: BankService
):CardService {

    @Throws(UserIsNotFoundException::class)
    override fun getAllCards(userEmail: String): List<Card> {
        val user: User = userRepository.findByEmail(userEmail)
            .orElseThrow { UserIsNotFoundException("User with email $userEmail is not registered!") }
        val cards = cardRepository.findCardsByOwnerId(user.id).filter { !it.deleted }
        return cards
    }

    override fun getCardById(cardId: Long): Card {
        TODO("Not yet implemented")
    }



    @Throws(
        EmptyCardNumberExceptions::class, WrongCardNumberFormatExceptions::class,
        CardExpiredException::class, WrongCvvCodeExceptions::class, FailedToRegisterCardException::class)
    override fun addNewCard(cardData: NewAddNewCardRequest, user: User): Card {

        if(cardData.cardNumber.isEmpty()) throw EmptyCardNumberExceptions()
        if(!isNumber(cardData.cardNumber) or (cardData.cardNumber.length != 16))
            throw WrongCardNumberFormatExceptions()

        if (cardData.dateExp.isBefore(LocalDateTime.now())) throw CardExpiredException()

        if(!bankService.checkData(cardData.cardNumber, cardData.dateExp, cardData.cvv))
            throw FailedToRegisterCardException()

        bankService.save(cardData.cardNumber, cardData.dateExp, cardData.cvv)

        val card = Card(
            lastFourDigits =  cardData.cardNumber.substring(12,16),
            bin = cardData.cardNumber[0].digitToInt(),
            owner = user
        )

        cardRepository.save(card)
        return card
    }

    @Throws(CardIsNotFoundExceptions::class)
    @Transactional
    override fun deleteCard(cardId: Long, user: User): Card {
        if(!cardRepository.findCardsByOwnerId(user.id).any{it.id == cardId}) throw CardIsNotFoundExceptions()

        val card = cardRepository.findCardById(cardId).orElse(null)

        card.deleted = true
        cardRepository.save(card)
        return card
    }

    fun isNumber(input: String): Boolean {
        val integerChars = '0'..'9'
        var dotOccurred = 0
        return input.all { it in integerChars || it == '.' && dotOccurred++ < 1 }
    }
}