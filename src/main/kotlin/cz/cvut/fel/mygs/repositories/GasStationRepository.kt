package cz.cvut.fel.mygs.repositories

import cz.cvut.fel.mygs.models.GasStation
import cz.cvut.fel.mygs.models.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface GasStationRepository: JpaRepository<GasStation, Long> {
    fun findGasStationById(id: Long): Optional<GasStation>
    fun findAllByCity(city: String): List<GasStation>

    @Query("SELECT a FROM GasStation a WHERE REPLACE(lower(CONCAT(a.street, " +
            "a.buildingNumber, " +
            "a.city, a.name)), ' ', '') LIKE REPLACE(CONCAT('%',lower(:title),'%'), ' ', '') " )
    fun findGasStationByCoincidence(title: String): List<GasStation>

    fun findAllByManagerId(manager_id: Long): List<GasStation>

}


