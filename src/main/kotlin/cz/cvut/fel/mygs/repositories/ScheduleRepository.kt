package cz.cvut.fel.mygs.repositories

import cz.cvut.fel.mygs.models.Schedule
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ScheduleRepository: JpaRepository<Schedule, Long> {

    fun findScheduleByDayOfWeekAndGasStationId(day:Int, gs_id:Long): Optional<Schedule>
}