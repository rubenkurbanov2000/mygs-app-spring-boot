package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.controllers.NewQuestionRequest
import cz.cvut.fel.mygs.models.Question
import cz.cvut.fel.mygs.repositories.QuestionRepository
import org.springframework.stereotype.Service

@Service
class QuestionServiceImpl(
    val questionRepository: QuestionRepository
) :QuestionService{


    override fun getAllQuestions(): List<Question> {
        return questionRepository.findAll()
    }

    override fun addNewQuestion(questionData: NewQuestionRequest) {

        val question: Question = Question(
            title = questionData.title,
            answer = questionData.answer
        )

        questionRepository.save(question)
    }

}