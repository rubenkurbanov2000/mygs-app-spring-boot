package cz.cvut.fel.mygs.assemblers

import cz.cvut.fel.mygs.models.Offer
import cz.cvut.fel.mygs.models.Order
import cz.cvut.fel.mygs.repositories.GasStationRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OfferAssembler(
    val gasStationRepository: GasStationRepository
) {

    fun toOfferDto(offer: Offer): OfferAssembler.OfferDto {
        return OfferAssembler.OfferDto(
            offer.id,
            offer.content,
            offer.imgUrl,
            offer.title,
            offer.gasStation!!.name,
            offer.gasStation!!.getAddres(),
            offer.gasStation!!.id
        )
    }

    fun toListOfferDto(offerList: List<Offer>): List<OfferDto>{
        return if(offerList.isNotEmpty()){
            offerList.map { toOfferDto(it) }
        }else{
            listOf()
        }
    }
    data class OfferDto(
        val id: Long,
        val content: String,
        val imgUrl: String,
        val title: String,
        val gsName: String,
        val gsAddress: String,
        val gsId: Long
    ) {
        override fun toString(): String {
            return "OfferDto(id=$id, content='$content', imgUrl='$imgUrl', title='$title', gsName='$gsName', gsAddress='$gsAddress')"
        }

    }


}
