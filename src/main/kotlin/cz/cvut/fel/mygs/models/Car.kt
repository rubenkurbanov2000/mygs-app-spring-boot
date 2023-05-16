package cz.cvut.fel.mygs.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*
import java.io.Serializable

@Entity
@Table(name = "car")
class Car(

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,

    @Column
    var brand: String = "",

    @Column
    var number: String = "",

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var owner: User? = null,

    @Column
    var deleted: Boolean = false,

    @Column(name = "fuel_type")
    @JsonProperty("fuel_type")
    var fuelType: FuelType = FuelType.DIESEL


): Serializable {

    fun fuelTypeFilterToId(): Int{

        if (fuelType == FuelType.DIESEL) return 0
        if (fuelType == FuelType.PETROL) return 1
        return 2

    }

}