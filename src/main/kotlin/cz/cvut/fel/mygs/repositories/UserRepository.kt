package cz.cvut.fel.mygs.repositories

import cz.cvut.fel.mygs.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun existsUserByEmail(@Param("email") email: String): Boolean
    override fun findById(id: Long): Optional<User>
    fun getUserById(id: Long) : User?
}