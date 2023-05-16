package cz.cvut.fel.mygs.repositories

import cz.cvut.fel.mygs.models.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, Long> {

    fun findByName(name: String): Role
}