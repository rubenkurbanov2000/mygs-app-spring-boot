package cz.cvut.fel.mygs.repositories

import cz.cvut.fel.mygs.models.Question
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionRepository: JpaRepository<Question, Long> {

    override fun findAll(): List<Question>

}