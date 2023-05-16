package cz.cvut.fel.mygs.controllers

import cz.cvut.fel.mygs.assemblers.UserAssembler
import cz.cvut.fel.mygs.auth.JwtResponse
import cz.cvut.fel.mygs.repositories.UserRepository
import cz.cvut.fel.mygs.services.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.*
import java.time.DayOfWeek
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("/user")
class UserController (
    val userService: UserService,
    val userRepository: UserRepository,
    val userAssembler: UserAssembler,
){
    companion object {
        private val LOG = LoggerFactory.getLogger(UserController::class.java)
    }

    @PostMapping("/reg")
    fun registrationNewUser(@RequestBody userRegistrationRequest: UserRegistrationRequest): ResponseEntity<UserAssembler.UserDto>{
        LOG.info(
            "REQUEST /user/reg: email - ${userRegistrationRequest.email} "
        )
        val user = userService.attemptRegistration(userRegistrationRequest)
        val response =userAssembler.toUserDto(user)

        LOG.info(
            "RESPONSE /user/reg: email - ${userRegistrationRequest.email}  SUCCESS"
        )

        return ResponseEntity.ok(response)
    }

    @PostMapping("/login")
    fun userAuthorization(@RequestBody userLogInReguest: UserLogInRequest): ResponseEntity<JwtResponse>{
        LOG.info(
            "REQUEST /user/login: email - ${userLogInReguest.email} "
        )
        val userResponseData: JwtResponse = userService.attemptAuthorization(userLogInReguest)
        LOG.info(
            "RESPONSE /user/login: email - ${userLogInReguest.email}  SUCCESS"
        )
        return ResponseEntity.ok(userResponseData)
    }

    @PostMapping("/verify_account")
    fun verifyAccountByCVerificationCode(@RequestBody userVerificationRequest: UserVerificationRequest): ResponseEntity<JwtResponse>{
        LOG.info(
            "REQUEST /user/login: email - ${userVerificationRequest.email} "
        )
        val userResponseData: JwtResponse = userService.attemptActivateVerificationCode(userVerificationRequest)
        LOG.info(
            "RESPONSE /user/login: email - ${userVerificationRequest.email}  SUCCESS Verified"
        )
        return ResponseEntity.ok(userResponseData)
    }

    @PostMapping("/{email}")
    fun setCodeForChangePassword(@PathVariable email: String,): ResponseEntity<String>{
        LOG.info(
            "REQUEST /user/{email}: email - $email "
        )
        userService.setVerificationCodeForResetPassword(email)
        LOG.info(
            "RESPONSE /user/{email}: A confirmation code has been sent to '$email' email"
        )
        return ResponseEntity.ok("A confirmation code has been sent to '$email' email")
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/reset_pas_auth")
    fun resetPasswordForAuthorizedUser(@RequestBody data: UserResetPasswordAuthRequest,
                                       fromUser: HttpServletRequest): ResponseEntity<String>{
        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)
        LOG.info(
            "REQUEST /user/reset_pas_auth: Reset password for user - ${user.email} "
        )
        userService.resetPasswordForAuthorizedUser(data, user.email)
        LOG.info(
            "RESPONSE /user/reset_pas: Reset password for user - ${user.email} SUCCESS"
        )
        return ResponseEntity.ok("The password has been successfully changed for the user - ${user.email}")
    }

    @PostMapping("/reset_pas")
    fun resetPasswordForUnauthorizedUser(@RequestBody data: UserResetPasswordRequest): ResponseEntity<String>{
        LOG.info(
            "REQUEST /user/reset_pas: Reset password for user - ${data.email} "
        )
        userService.resetPasswordForUnauthorizedUser(data)
        LOG.info(
            "RESPONSE /user/reset_pas: Reset password for user - ${data.email} SUCCESS"
        )
        return ResponseEntity.ok("The password has been successfully changed for the user - ${data.email}")
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update_phone")
    fun updatePhoneNumber(@RequestBody data: UserUpdatePhoneRequest,
                          fromUser: HttpServletRequest): ResponseEntity<String>{

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)
        LOG.info(
            "REQUEST /user/update_phone: Update phone number for user - ${user.email}" +
                    " to ${data.newPhone}"
        )
        userService.updatePhoneNumber(data, user.email)
        LOG.info(
            "RESPONSE /user/update_phone: Reset password for user - ${user.email} " +
                    "to ${data.newPhone} SUCCESS"
        )
        return ResponseEntity.ok("The phone number has been successfully changed for the user - ${user.email}")
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update_email_req")
    fun getVerificationCodeToChangeEmail(@RequestBody data :UserUpdateEmailGetCodeRequest ,fromUser: HttpServletRequest): ResponseEntity<String>{

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)
        LOG.info(
            "REQUEST /user/update_email_req: Get verification code to change email for user - ${user.email}"
        )
        userService.getVerificationCodeToChangeEmail(user.email, data.newEmail)
        LOG.info(
            "RESPONSE /user/update_email_req: Get verification code to change email for user - ${user.email} SUCCESS"
        )
        return ResponseEntity.ok("The code has been sent to email - ${data.newEmail}")
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update_email")
    fun updateEmail(@RequestBody data :UserUpdateEmailRequest ,fromUser: HttpServletRequest): ResponseEntity<String>{

        val user = userRepository.findByEmail(fromUser.userPrincipal.name).orElse(null)
        LOG.info(
            "REQUEST /user/update_email_req: Update email for user - ${user.email} to" +
                    "${data.newEmail}"

        )
        userService.updateEmail(data, user.email)
        LOG.info(
            "RESPONSE /user/update_email_req: Update email for user - ${user.email} to" +
                    " ${data.newEmail} SUCCESS"
        )
        return ResponseEntity.ok("The email has been successfully changed for - ${data.newEmail}")
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/manager")
    fun createManagerAccount(@RequestBody data :UserRegistrationRequest ,fromUser: HttpServletRequest): ResponseEntity<UserAssembler.UserDto>{

        LOG.info(
            "REQUEST /user/manager: Administrator - Creating a new manager ${data.email}"
        )
        val user = userService.attemptRegistration(data)
        val response = userAssembler.toUserDto(user)
        LOG.info(
            "RESPONSE /user/manager: Administrator - Creating a new manager ${data.email} SUCCESS"
        )
        return ResponseEntity.ok(response)
    }
}

data class UserRegistrationRequest(
    val name: String,
    val surname: String,
    val email: String,
    val phone: String,
    val dob: LocalDateTime,
    val password: String,
    val gender: Int)

data class UserLogInRequest(
    val email: String,
    val password: String)

data class UserVerificationRequest(
    val email: String,
    val code: Int,
    val password: String
)
data class UserResetPasswordRequest(
    val email: String,
    val code: Int,
    val password: String,
    val passwordAgain: String
)

data class UserResetPasswordAuthRequest(
    val curPassword: String,
    val newPassword: String,
    val newPasswordAgain: String
)

data class UserUpdateEmailRequest(
    val newEmail: String,
    val code: Int,
)
data class UserUpdatePhoneRequest(
    val newPhone: String,
)
data class UserUpdateEmailGetCodeRequest(
    val newEmail: String,
)
