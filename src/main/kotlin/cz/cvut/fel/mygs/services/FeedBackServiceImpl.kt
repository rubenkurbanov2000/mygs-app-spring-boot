package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.exceptios.ContentOfFeedbackCanNotBeEmptyException
import cz.cvut.fel.mygs.exceptios.GasStationIsNotFoundException
import cz.cvut.fel.mygs.exceptios.OrderIsNotFoundExceptions
import cz.cvut.fel.mygs.exceptios.TitleOfFeedbackCanNotBeEmptyException
import cz.cvut.fel.mygs.exceptios.TitleOfFeedbackIsSoLongException
import cz.cvut.fel.mygs.controllers.LeaveFeedbackRequest
import cz.cvut.fel.mygs.models.Feedback
import cz.cvut.fel.mygs.models.User
import cz.cvut.fel.mygs.repositories.FeedBackRepository
import cz.cvut.fel.mygs.repositories.GasStationRepository
import cz.cvut.fel.mygs.repositories.OrderRepository
import cz.cvut.fel.mygs.repositories.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.jvm.Throws

@Service
class FeedBackServiceImpl(
    val feedBackRepository: FeedBackRepository,
    val userRepository: UserRepository,
    val gasStationRepository: GasStationRepository,
    val orderRepository: OrderRepository,
    val mailService: MailService
):FeedBackService {

    override fun getFeedBackFromUserToGasStationIfExist(gs_id: Long, user: User): Feedback? {
        return feedBackRepository.findByAuthorIdAndBelongsGsId(user.id, gs_id).orElse(null)
    }

    override fun getFeedBackFromUserToOrderIfExist(order_id: Long, user: User): Feedback? {
        return feedBackRepository.findByAuthorIdAndBelongsOrderId(user.id, order_id).orElse(null)
    }

    @Throws(
        ContentOfFeedbackCanNotBeEmptyException::class, TitleOfFeedbackCanNotBeEmptyException::class,
        GasStationIsNotFoundException::class, TitleOfFeedbackIsSoLongException::class)
    override fun leaveFeedbackAboutGS(gs_id: Long, user: User, data: LeaveFeedbackRequest): Feedback {

        val feedback = feedBackRepository.findByAuthorIdAndBelongsGsId(user.id, gs_id).orElse(null)

        val gs = gasStationRepository.findById(gs_id).orElse(null)

        if(data.content.isEmpty()) throw ContentOfFeedbackCanNotBeEmptyException()
        if(data.title.isEmpty()) throw TitleOfFeedbackCanNotBeEmptyException()
        if((data.title.length < 2) or (data.title.length>20)) throw TitleOfFeedbackIsSoLongException()
        if(gs == null) throw GasStationIsNotFoundException()
        val newFeedback: Feedback = Feedback(
            content = data.content,
            title = data.title,
            created = LocalDateTime.now(),
            rating = data.rating,
            isUpdated = feedback != null,
            belongsGs = gs,
            author = user
        )
        feedBackRepository.save(feedback)

        mailService.send(user.email, "MyGs Feedback: ${feedback.title}", "" +
                "You have just left a review for the ${gs.name} gas station, which is located at - ${gs.getAddres()}.\n" +
                "\n" +
                "Review content - ${feedback.content}.\n" +
                "\n" +
                "Thanks for your feedback! You help us get better. The operator will respond to your feedback as soon as possible.")

        gs.rating = getArithmeticMeanOfRating(feedBackRepository.findAllByBelongsGsId(gs_id))
        gasStationRepository.save(gs)

        return feedback
    }

    @Throws(
        ContentOfFeedbackCanNotBeEmptyException::class, TitleOfFeedbackCanNotBeEmptyException::class,
        OrderIsNotFoundExceptions::class, TitleOfFeedbackIsSoLongException::class)
    override fun leaveFeedbackAboutOrder(order_id: Long, user: User, data: LeaveFeedbackRequest): Feedback {

        val feedback = feedBackRepository.findByAuthorIdAndBelongsOrderId(user.id, order_id).orElse(null)

        val order = orderRepository.findById(order_id).orElse(null)

        if(data.content.isEmpty()) throw ContentOfFeedbackCanNotBeEmptyException()
        if(data.title.isEmpty()) throw TitleOfFeedbackCanNotBeEmptyException()
        if((data.title.length < 2) or (data.title.length>20)) throw TitleOfFeedbackIsSoLongException()
        if(order == null) throw OrderIsNotFoundExceptions()
        val newFeedback: Feedback = Feedback(
            content = data.content,
            title = data.title,
            created = LocalDateTime.now(),
            rating = data.rating,
            isUpdated = feedback != null,
            belongsOrder = order,
            author = user
        )
        feedBackRepository.save(feedback)

        mailService.send(user.email, "MyGs Feedback: ${feedback.title}", "" +
                "You have just left a review for the order with id ${order.id} which was - ${order.created.dayOfMonth.toString() + '.' + order.created.month.value.toString() + '.'+ order.created.year.toString()}.\n" +
                "\n" +
                "Review content - ${feedback.content}.\n" +
                "\n" +
                "Thanks for your feedback! You help us get better. The operator will respond to your feedback as soon as possible.")

        return feedback
    }

    fun getArithmeticMeanOfRating(feedbacks: List<Feedback>): Double{
        val ratingValues: MutableList<Int> = mutableListOf()

        for (i in feedbacks)
            ratingValues.add(i.rating)
        return ratingValues.average()
    }

}