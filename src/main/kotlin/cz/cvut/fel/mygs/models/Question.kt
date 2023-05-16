package cz.cvut.fel.mygs.models

import javax.persistence.*
import java.io.Serializable


@Entity
@Table(name = "question")
class Question (
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id:Long = 0,

    @Column(nullable = false)
    val title: String = "",

    @Column(nullable = false)
    val answer: String = "",



): Serializable {

}