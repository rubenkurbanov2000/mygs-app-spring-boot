package cz.cvut.fel.mygs.models

import javax.persistence.*
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime


@Entity
@Table(name = "feedback")
class Feedback(
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,

    @Column(name = "content")
    var content: String = "",

    @Column(name = "title")
    var title: String = "",

    @Column(name = "created")
    @DateTimeFormat
    var created: LocalDateTime = LocalDateTime.now(),

    @Column(name = "rating")
    var rating: Int = 0,

    @Column(name = "isUpdated")
    var isUpdated: Boolean = false,

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    var belongsOrder: Order? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "gas_station_id", referencedColumnName = "id")
    var belongsGs: GasStation? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var author: User? = null,
)