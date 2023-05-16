package cz.cvut.fel.mygs.repositories

import cz.cvut.fel.mygs.models.Car
import cz.cvut.fel.mygs.models.Card
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CardRepository: JpaRepository<Card, Long> {

    override fun findAll(): List<Card>
    fun findCardById(id: Long): Optional<Card>

    fun findCardsByOwnerId(userId: Long): List<Card>
    fun deleteCardById(id: Long): Card
}