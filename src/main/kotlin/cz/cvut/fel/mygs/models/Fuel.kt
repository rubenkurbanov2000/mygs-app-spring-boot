package cz.cvut.fel.mygs.models

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "fuel")
class Fuel(
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,

    @Column
    var name: String = "",

    @Column
    var price: Double = 0.0,

    @JsonBackReference
    @ManyToOne(optional = false)
    @JoinColumn(name = "gas_station_id", referencedColumnName = "id")
    var gasStation: GasStation? = null,

    @OneToOne (optional=false, mappedBy="fuel")
    var order: Order? = null,

    @Column(name = "fuel_type")
    @JsonProperty("fuel_type")
    var fuelType: FuelType = FuelType.DIESEL,


):Serializable {

    @ManyToMany(mappedBy = "fuels", cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    var rs: MutableList<RefuelingStand> = mutableListOf()

//    @ManyToMany(mappedBy = "fuels", cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
//    var rs: MutableList<RefuelingStand> = mutableListOf()

    fun fuelTypeFilterToId(): Int{

        if (fuelType == FuelType.DIESEL) return 0
        if (fuelType == FuelType.PETROL) return 1
        return 2
    }

    override fun toString(): String {
        return "Fuel(id=$id, name='$name', price=$price, gasStation=$gasStation, order=$order, fuelType=$fuelType)"
    }


}

enum class FuelType{
    DIESEL, PETROL, PROPANE
}

fun fuelTypeFilterToEnum(type: Int): FuelType{

    if (type == 0) return FuelType.DIESEL
    if (type == 1) return FuelType.PETROL
    return FuelType.PROPANE
}