package cz.cvut.fel.mygs.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*

@Entity
@Table(name = "offer")
class Offer(

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,

    @Column
    var content: String ="",

    @Column(name = "img_url")
    @JsonProperty("img_url")
    var imgUrl: String = "",

    @Column
    var title: String ="",

    @Column
    var isDeleted: Boolean = false,

    @ManyToOne(optional = false)
    @JoinColumn(name = "gas_station_id", referencedColumnName = "id")
    var gasStation: GasStation? = null,

) {
    override fun toString(): String {
        return "Offer(id=$id, content='$content', imgUrl='$imgUrl', title='$title')"
    }
}