package cz.cvut.fel.mygs.repositories

import cz.cvut.fel.mygs.models.Car
import cz.cvut.fel.mygs.models.Offer
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OfferRepository: JpaRepository<Offer, Long> {
    override fun findAll():List<Offer>

    fun findByIdAndGasStationId(offer_id: Long, gs_id:Long): Optional<Offer>
}