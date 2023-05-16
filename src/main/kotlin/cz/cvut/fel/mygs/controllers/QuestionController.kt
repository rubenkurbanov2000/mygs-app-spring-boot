package cz.cvut.fel.mygs.controllers

import cz.cvut.fel.mygs.models.Question
import cz.cvut.fel.mygs.repositories.QuestionRepository
import cz.cvut.fel.mygs.services.QuestionService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/question")
class QuestionController(

    val questionRepository: QuestionRepository,
    val questionService: QuestionService
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(QuestionController::class.java)
    }

    @GetMapping("/")
    fun getAllQuestions(): ResponseEntity<List<Question>> {
        LOG.info(
            "REQUEST /question/: Get - List of all questions"
        )
        val questions = questionService.getAllQuestions()

        LOG.info(
            "RESPONSE /question/: Get - List of all questions - SUCCESS"
        )
        return ResponseEntity.ok(questions)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    fun addNewQuestion(@RequestBody newQuestionRequest: NewQuestionRequest, request: HttpServletRequest): ResponseEntity<String> {
        LOG.info(
            "REQUEST /question/: Add new question"
        )

        questionService.addNewQuestion(newQuestionRequest)

        LOG.info(
            "RESPONSE /question/: Add new question - SUCCESS"
        )
        return ResponseEntity.ok("Successfully added")
    }
}

data class NewQuestionRequest(
    val title: String,
    val answer: String
)