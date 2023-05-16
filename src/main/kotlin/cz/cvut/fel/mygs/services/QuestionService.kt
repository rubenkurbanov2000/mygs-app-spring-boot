package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.controllers.NewQuestionRequest
import cz.cvut.fel.mygs.models.Question

interface QuestionService {

    fun getAllQuestions(): List<Question>
    fun addNewQuestion(questionData: NewQuestionRequest)
}