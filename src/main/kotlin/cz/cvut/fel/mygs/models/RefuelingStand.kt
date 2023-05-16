package cz.cvut.fel.mygs.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*
import java.io.Serializable


@Entity
@Table(name = "`refueling_stand`")
class RefuelingStand(
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,

    @Column(name = "stand_id")
    @JsonProperty("stand_id")
    var standId: Int = 0,

    @ManyToOne(optional = false)
    @JoinColumn(name = "gas_station_id", referencedColumnName = "id")
    var gasStation: GasStation? = null,

    @ManyToMany(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    @JoinTable(
        name = "rs_fuel",
        joinColumns = [JoinColumn(name = "rs_id")],
        inverseJoinColumns = [JoinColumn(name = "fuel_id")]
    )
    var fuels: MutableList<Fuel> = mutableListOf()

    ) : Serializable{

    override fun toString(): String {
        return "RefuelingStand(id=$id, standId=$standId, gasStation=$gasStation)"
    }

//    @ManyToMany(mappedBy = "rs", cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
//    var fuels: MutableList<Fuel> = mutableListOf()
}