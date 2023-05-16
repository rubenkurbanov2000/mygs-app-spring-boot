package cz.cvut.fel.mygs.assemblers

import cz.cvut.fel.mygs.models.Card
import cz.cvut.fel.mygs.models.Feedback
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class FeedBackAssembler {

    fun toFeedBackDto(feedBack: Feedback): FeedBackAssembler.FeedBackDto {
        return FeedBackAssembler.FeedBackDto(

            feedBack.id,
            feedBack.content,
            feedBack.title,
            feedBack.rating,
            feedBack.created,
            feedBack.isUpdated
        )
    }

    fun toListFeedBackDto(feedBackList: List<Feedback>): List<FeedBackAssembler.FeedBackDto>{
        return if(feedBackList.isNotEmpty()){
            feedBackList.map { toFeedBackDto(it) }
        }else{
            listOf()
        }
    }
    data class FeedBackDto(
        val id: Long,
        val content: String,
        val title: String,
        val rating: Int,
        val created: LocalDateTime,
        val isUpdated: Boolean
    ) {
        override fun toString(): String {
            return "FeedBackDto(id=$id, content='$content', title='$title', rating=$rating, created=$created, isUpdated=$isUpdated)"
        }
    }

}
