package cz.cvut.fel.mygs.models

import javax.persistence.*

@Entity
@Table(name = "`role`")
class Role(
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,
    @Column
    var name: String = "USER",
) {
}
