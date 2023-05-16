package cz.cvut.fel.mygs.assemblers

import cz.cvut.fel.mygs.models.Card
import org.springframework.stereotype.Component

@Component
class CardAssembler {

    fun toCardDto(card: Card): CardAssembler.CardDto {
        return CardAssembler.CardDto(

            card.id,
            card.lastFourDigits,
            card.bin

        )
    }

    fun toListCardDto(cardList: List<Card>): List<CardAssembler.CardDto>{
        return if(cardList.isNotEmpty()){
            cardList.map { toCardDto(it) }
        }else{
            listOf()
        }
    }
    data class CardDto(val id: Long,
                      val lastFourDigits: String,
                      val bin: Int
    )


}

