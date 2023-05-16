package cz.cvut.fel.mygs.assemblers

import cz.cvut.fel.mygs.models.Gender
import cz.cvut.fel.mygs.models.User
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserAssembler {

    fun toUserDto(user: User): UserDto{
        return UserDto(
            user.id,
            user.email,
            user.name,
            user.surname,
            user.dob,
            user.phoneNumber,
            genderFilter(user.gender)
            )
    }

    fun toListUserDto(userList: List<User>): List<UserDto>{
        return if(userList.isNotEmpty()){
            userList.map { toUserDto(it) }
        }else{
            listOf()
        }
    }

    data class UserDto(val id: Long,
                       val email: String,
                       val name: String,
                       val surname: String,
                       val dob: LocalDateTime,
                       val phone: String,
                       val gender: Int
                        )

    fun genderFilter(gender: Gender): Int{

        return if (gender == Gender.MALE) 0
        else 1
    }

}