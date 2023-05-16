package cz.cvut.fel.mygs.auth

import java.time.LocalDateTime


class JwtResponse(
    val id: Long,
    val email: String,
    val name: String,
    val surname: String,
    val phone: String,
    val dob: LocalDateTime,
    val gender: Int,
    val accessToken: String,

) {
    var type = "Bearer"
}