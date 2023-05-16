package cz.cvut.fel.mygs.models

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "`schedule`")
class Schedule(
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,

    @Column(name = "day_of_week")
    var dayOfWeek: Int? = -1,

    @Column(name = "open_from")
    var openFrom: LocalDateTime? = null,

    @Column(name = "open_to")
    var openTo: LocalDateTime? = null,

    @Column(name = "is_closed")
    var isClosed: Boolean = false,

    @Column(name = "is_around_the_clock")
    var isAroundTheClock: Boolean = false,

    @ManyToOne(optional = false)
    @JoinColumn(name = "gas_station_id", referencedColumnName = "id")
    var gasStation: GasStation? = null,

) {


}