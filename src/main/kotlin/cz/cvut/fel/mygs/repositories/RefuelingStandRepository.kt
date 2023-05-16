package cz.cvut.fel.mygs.repositories

import cz.cvut.fel.mygs.models.RefuelingStand
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RefuelingStandRepository: JpaRepository<RefuelingStand, Long> {

    fun findAllByGasStationId(gs_id: Long): List<RefuelingStand>
    fun findRefuelingStandByStandIdAndGasStationId(rs_id: Int, gs_id: Long): Optional<RefuelingStand>
}