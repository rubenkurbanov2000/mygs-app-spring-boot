package cz.cvut.fel.mygs.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*

import java.io.Serializable

@Entity
@Table(name = "card")
class Card (


    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,

    @Column
    var bin: Int = 0,

    @Column(name="last_four_digits")
    @JsonProperty("last_four_digits")
    var lastFourDigits: String = "",

    @Column
    var deleted: Boolean = false,

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var owner: User? = null,

    ): Serializable {

        }