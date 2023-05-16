package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.controllers.LeaveFeedbackRequest
import cz.cvut.fel.mygs.models.Feedback
import cz.cvut.fel.mygs.models.User

interface FeedBackService {

    fun getFeedBackFromUserToGasStationIfExist(gs_id: Long, user: User): Feedback?
    fun getFeedBackFromUserToOrderIfExist(order_id: Long,user: User): Feedback?
    fun leaveFeedbackAboutGS(gs_id: Long, user: User, data: LeaveFeedbackRequest): Feedback
    fun leaveFeedbackAboutOrder(order_id: Long,user: User, data: LeaveFeedbackRequest): Feedback

}