package cz.cvut.fel.mygs.services

import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.LocalDateTime


/**
 * Banking. Implementation for this block is not requested.
 *
 * @param cardData of user new card data
 *
 */
@Service
class BankService(val cardData: CardData) {

    /**
     * Sending data to the bank for authentication.
     *
     * @param
     * @return Boolean (Always true for this version of app)
     */
    fun checkData(cardNumber: String, cardExpirationDate: LocalDateTime, cardCvv: Int):Boolean {
        return cardData.check(cardNumber, cardExpirationDate, cardCvv)
    }

    /**
     * Saving card data and subsequent work with it
     *
     * @params cardNumber: String
     *         cardExpirationDate: LocalDateTime
     *         cardCvv: Int
     * @return void
     */
    fun save(cardNumber: String, cardExpirationDate: LocalDateTime, cardCvv: Int){
        cardData.save(cardNumber,cardExpirationDate, cardCvv)
    }

    /**
     * Refueling payment
     *
     * @param amount: Double
     * @return Boolean If the checkout was successful (Always true for this version of app)
     */
    fun payForRefueling(amount: Double): Boolean{
        return cardData.pay(amount)
    }

}

@Component
public class CardData (

){

    fun save(cardNumber: String, cardExpirationDate:LocalDateTime, cardCvv: Int){}
    fun check(cardNumber: String, cardExpirationDate:LocalDateTime, cardCvv: Int): Boolean {return true}
    fun pay(amount: Double): Boolean {return true}
}