package cz.cvut.fel.mygs.repositories

import cz.cvut.fel.mygs.models.Offer
import cz.cvut.fel.mygs.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface ManagerRepository: JpaRepository<User, Long> {

}