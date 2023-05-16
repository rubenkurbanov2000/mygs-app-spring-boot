package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.controllers.CarAddNewRequest
import cz.cvut.fel.mygs.controllers.NewAddNewCardRequest
import cz.cvut.fel.mygs.models.Car
import cz.cvut.fel.mygs.models.Card
import cz.cvut.fel.mygs.models.User

interface CardService {


    fun getAllCards(userEmail: String): List<Card>
    fun getCardById(cardId: Long): Card
    fun addNewCard(cardData: NewAddNewCardRequest, user: User): Card
    fun deleteCard(cardId: Long, user: User): Card
}