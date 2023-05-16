package cz.cvut.fel.mygs.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*
import javax.validation.constraints.Pattern
import org.springframework.format.annotation.DateTimeFormat
import java.io.Serializable
import java.time.LocalDateTime



@Entity
@Table(name = "users")
class User(

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,

    @Column(name = "dob")
    @DateTimeFormat
    var dob: LocalDateTime = LocalDateTime.now(),

    @Column(unique = true, name = "email")
    @JsonProperty("email")
    var email: String = "",

    @Column
    var name: String = "",

    @Column
    var surname: String = "",

    @Column
    @JsonProperty("password")
    var password: String = "",

    @Column(name = "code_activated")
    var codeActivated: Boolean = false,

    @Column(name = "phone_number")
    @Pattern(regexp = "^\\+[0-9]{10,13}\$")
    var phoneNumber: String = "",

    @Column
    var gender: Gender = Gender.MALE,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roles: Collection<Role>? = null,

    @Column(name = "code")
    var code: Int = -1,

    ): Serializable{
    @OneToMany(mappedBy = "owner", targetEntity = Car::class)
    private var ownCars: Collection<Car>? = null

    @OneToMany(mappedBy = "owner", targetEntity = Card::class)
    private var ownCards: Collection<Card>? = null

    @OneToMany(mappedBy = "client", targetEntity = Order::class)
    private var orders: Collection<Order>? = null

    @OneToMany(mappedBy = "author", targetEntity = Feedback::class)
    private var feedbacks: Collection<Feedback>? = null

    @OneToMany(mappedBy = "manager", targetEntity = GasStation::class)
    private var manage: Collection<GasStation>? = null

    @ManyToMany(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_favorite",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "gs_id")]
    )
    var favorite: MutableList<GasStation> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}


enum class Gender {
    MALE, FEMALE
}