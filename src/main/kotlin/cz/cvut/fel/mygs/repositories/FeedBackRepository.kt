package cz.cvut.fel.mygs.repositories

import cz.cvut.fel.mygs.models.Car
import cz.cvut.fel.mygs.models.Feedback
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface FeedBackRepository: JpaRepository<Feedback, Long> {
    fun findByAuthorIdAndBelongsOrderId(authorId:Long, order_id:Long): Optional<Feedback>
    fun findByAuthorIdAndBelongsGsId(authorId:Long, gs_id:Long): Optional<Feedback>
    fun findAllByBelongsGsId(gs_id:Long): List<Feedback>

}