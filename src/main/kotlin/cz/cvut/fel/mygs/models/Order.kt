package cz.cvut.fel.mygs.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*
import org.springframework.format.annotation.DateTimeFormat
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order (

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,

    @Column(name = "count_litters")
    @JsonProperty("count_litters")
    var countLitters: Double = 0.0,

    @Column(name = "total_amount")
    @JsonProperty("total_amount")
    var totalAmount: Double = 0.0,

    @Column(name = "created")
    @DateTimeFormat
    var created: LocalDateTime = LocalDateTime.now(),

    @Column(name = "refueled_car_number")
    @JsonProperty("refueled_car_number")
    var refueledCarNumber: String = "",

    @Column(name = "refueled_card_number")
    @JsonProperty("refueled_card_number")
    var refueledCardNumber: String = "",

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var client: User? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "gas_station_id", referencedColumnName = "id")
//    @JsonProperty("gas_station")
    var gasStation: GasStation? = null,

    @OneToOne(optional = false, cascade = [CascadeType.ALL])
    @JoinColumn(name = "fuel_id")
    var fuel: Fuel? = null,

): Serializable{
    @OneToMany(mappedBy = "belongsOrder", targetEntity = Feedback::class)
    private var feedbacks: Collection<Feedback>? = null

}