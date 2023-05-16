package cz.cvut.fel.mygs.repositories

import cz.cvut.fel.mygs.models.Fuel
import cz.cvut.fel.mygs.models.GasStation
import cz.cvut.fel.mygs.models.RefuelingStand
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface FuelRepository: JpaRepository<Fuel, Long> {

    fun findFuelById(id: Long): Optional<Fuel>
    fun findAllByGasStationId(gs_id: Long): List<Fuel>
    fun findFuelByIdAndGasStationId(fuel_id: Long, gs_id: Long): Optional<Fuel>

    @Query("SELECT f FROM Fuel f join f.rs s where s.standId =:id")
    fun findAllByRsContainsRefuelingStand(id: Int): List<Fuel>

}