package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.exceptios.InvalidEmailFormatException
import cz.cvut.fel.mygs.exceptios.InvalidPasswordExceptions
import cz.cvut.fel.mygs.exceptios.InvalidPhoneFormatException
import cz.cvut.fel.mygs.exceptios.InvalidUserDataException
import cz.cvut.fel.mygs.exceptios.InvalidVerificationCodeException
import cz.cvut.fel.mygs.exceptios.PasswordMismatchExceptions
import cz.cvut.fel.mygs.exceptios.ShortPasswordException
import cz.cvut.fel.mygs.exceptios.UnsuccessfulAuthorizationException
import cz.cvut.fel.mygs.exceptios.UserAlreadyRegisteredException
import cz.cvut.fel.mygs.exceptios.UserHasAlreadyVerifiedException
import cz.cvut.fel.mygs.exceptios.UserIsNotFoundException
import cz.cvut.fel.mygs.exceptios.VerificationCodeNotConfirmedExceptions
import cz.cvut.fel.mygs.auth.JwtProvider
import cz.cvut.fel.mygs.auth.JwtResponse
import cz.cvut.fel.mygs.controllers.*
import cz.cvut.fel.mygs.models.Gender
import cz.cvut.fel.mygs.models.Role
import cz.cvut.fel.mygs.models.User
import cz.cvut.fel.mygs.repositories.RoleRepository
import cz.cvut.fel.mygs.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.regex.Pattern
import javax.annotation.PostConstruct
import kotlin.random.Random

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
    val mailService: MailService,
    val roleRepository: RoleRepository
) : UserService {

    @Value("mygs.admin.com")
    lateinit var adminUser: String

    @Value("mygs.manager.com")
    lateinit var managerUser: String

    @Autowired
    lateinit var encoder: PasswordEncoder

    @Autowired
    lateinit var jwtProvider: JwtProvider

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @PostConstruct
    fun init() {
        if (roleRepository.count() == 0L) {
            roleRepository.saveAll(
                listOf(
                    Role(name = "ROLE_ADMIN"),
                    Role(name = "ROLE_USER"),
                    Role(name = "ROLE_MANAGER"),
                )
            )
        }
    }

    @Throws(
        UserAlreadyRegisteredException::class, InvalidEmailFormatException::class,
        InvalidPhoneFormatException::class, InvalidUserDataException::class, ShortPasswordException::class)
    override fun attemptRegistration(userDetails: UserRegistrationRequest): User {

        if(userRepository.findByEmail(userDetails.email).isPresent){
            throw UserAlreadyRegisteredException("User with email ' ${userDetails.email} 'is already registered!")
        }else{
            if(isEmailValid(userDetails.email)){
                if (isPhoneValid(userDetails.phone)){

                    if (userDetails.name.isEmpty() ||
                        userDetails.surname.isEmpty() ||
                        userDetails.dob.year >= LocalDateTime.now().year)
                            throw InvalidUserDataException()

                    if(userDetails.password.length <= 5) throw ShortPasswordException()

                    val newUser = User(
                        dob = userDetails.dob,
                        email = userDetails.email,
                        name = userDetails.name,
                        surname = userDetails.surname,
                        password = encoder.encode(userDetails.password),
                        phoneNumber = userDetails.phone,
                        gender = genderFilterToEnum(userDetails.gender),
                        code = Random.nextInt(1000, 9999)
                    )

                    when(userDetails.email.substringAfter('@')){

                        adminUser -> newUser.roles = listOf(
                            roleRepository.findByName("ROLE_ADMIN"),
                            roleRepository.findByName("ROLE_USER"),
                            roleRepository.findByName("ROLE_MANAGER")
                        )
                        managerUser -> newUser.roles = listOf(
                            roleRepository.findByName("ROLE_MANAGER"),
                            roleRepository.findByName("ROLE_USER"),
                        )
                        else -> {
                            newUser.roles = listOf(roleRepository.findByName("ROLE_USER"))
                            mailService.send(userDetails.email, "My GasStation Registration", " Your verification code for registration: ${newUser.code.toString()}")
                        }
                    }

                    userRepository.save(newUser)
                    return newUser

                }else{
                    throw InvalidPhoneFormatException()
                }

            }else{
                throw InvalidEmailFormatException()
            }
        }

    }

    @Throws(
        UserIsNotFoundException::class, ShortPasswordException::class,
        UnsuccessfulAuthorizationException::class, VerificationCodeNotConfirmedExceptions::class)
    override fun attemptAuthorization(userDetails: UserLogInRequest): JwtResponse{

        val user: User = userRepository.findByEmail(userDetails.email)
            .orElseThrow{ UserIsNotFoundException("User with email ${userDetails.email} is not registered!") }

        //If the user has not verified their email
        if(!user.codeActivated) throw VerificationCodeNotConfirmedExceptions()

        if(userDetails.password.length < 5) throw ShortPasswordException()

        try {
            SecurityContextHolder.getContext().authentication =
                authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(userDetails.email, userDetails.password)
                )
            val token: String =jwtProvider.generateJwtToken(user.email)

            return JwtResponse(
                id = user.id,
                name = user.name,
                surname = user.surname,
                dob = user.dob,
                phone = user.phoneNumber,
                email = user.email,
                gender = genderFilterFromEnum(user.gender),
                accessToken = token
            )

        }catch (e: Exception){
            throw UnsuccessfulAuthorizationException()
        }

    }

    @Throws(UserIsNotFoundException::class, UserHasAlreadyVerifiedException::class, InvalidVerificationCodeException::class)
    override fun attemptActivateVerificationCode(userDetails: UserVerificationRequest): JwtResponse {
        val user: User = userRepository.findByEmail(userDetails.email)
            .orElseThrow{ UserIsNotFoundException("User with email ${userDetails.email} is not registered!") }
        if (user.codeActivated) throw UserHasAlreadyVerifiedException()
        if(user.code != userDetails.code) throw InvalidVerificationCodeException()
        else {
            try {
                SecurityContextHolder.getContext().authentication =
                    authenticationManager.authenticate(
                        UsernamePasswordAuthenticationToken(userDetails.email, userDetails.password)
                    )
                val token: String =jwtProvider.generateJwtToken(user.email)

                // Activate user account
                user.codeActivated = true
                user.code = -1
                userRepository.save(user)

                // LogIn User
                return JwtResponse(
                    id = user.id,
                    name = user.name,
                    surname = user.surname,
                    dob = user.dob,
                    phone = user.phoneNumber,
                    email = user.email,
                    gender = genderFilterFromEnum(user.gender),
                    accessToken = token
                )

            }catch (e: Exception){
                throw UnsuccessfulAuthorizationException()
            }
        }
    }

    @Throws(UserIsNotFoundException::class, VerificationCodeNotConfirmedExceptions::class)
    override fun setVerificationCodeForResetPassword(email: String) {
        val user: User = userRepository.findByEmail(email)
            .orElseThrow{ UserIsNotFoundException("User with email $email is not registered!") }
        if(!user.codeActivated)  throw VerificationCodeNotConfirmedExceptions()
        else{
            user.code = Random.nextInt(1000, 9999)
            userRepository.save(user)
            mailService.send(email, "My GasStation Reset Password", " Your password reset verification code: ${user.code.toString()}")
        }
    }

    @Throws(
        UserIsNotFoundException::class, VerificationCodeNotConfirmedExceptions::class,
        PasswordMismatchExceptions::class, InvalidVerificationCodeException::class)
    override fun resetPasswordForUnauthorizedUser(userData: UserResetPasswordRequest) {
        val user: User = userRepository.findByEmail(userData.email)
            .orElseThrow{ UserIsNotFoundException("User with email ${userData.email} is not registered!") }
        if(!user.codeActivated) throw VerificationCodeNotConfirmedExceptions()
        if(userData.password != userData.passwordAgain) throw PasswordMismatchExceptions()
        if (user.code != userData.code) throw InvalidVerificationCodeException()

        user.password = encoder.encode(userData.password)
        user.code = -1
        userRepository.save(user)
    }

    @Throws(
        UserIsNotFoundException::class, InvalidPasswordExceptions::class,
        PasswordMismatchExceptions::class)
    override fun resetPasswordForAuthorizedUser(data: UserResetPasswordAuthRequest, userEmail: String) {

        val user: User = userRepository.findByEmail(userEmail)
            .orElseThrow{ UserIsNotFoundException("User with email $userEmail is not registered!") }

        if (!encoder.matches(data.curPassword, user.password)) throw InvalidPasswordExceptions()
        if(data.newPassword != data.newPasswordAgain) throw PasswordMismatchExceptions()

        user.password = encoder.encode(data.newPassword)
        userRepository.save(user)
    }

    @Throws(UserIsNotFoundException::class, InvalidPhoneFormatException::class)
    override fun updatePhoneNumber(newPhoneData: UserUpdatePhoneRequest, userEmail: String) {
        val user: User = userRepository.findByEmail(userEmail)
            .orElseThrow{ UserIsNotFoundException("User with email $userEmail is not registered!") }

        if(!isPhoneValid(newPhoneData.newPhone)) throw InvalidPhoneFormatException()

        user.phoneNumber = newPhoneData.newPhone
        userRepository.save(user)
    }
    @Throws(UserIsNotFoundException::class, InvalidEmailFormatException::class)
    override fun getVerificationCodeToChangeEmail(userEmail: String, newEmail: String) {
        val user: User = userRepository.findByEmail(userEmail)
            .orElseThrow{ UserIsNotFoundException("User with email $userEmail is not registered!") }
        if(!isEmailValid(newEmail)) throw InvalidEmailFormatException()
        user.code = Random.nextInt(1000, 9999)
        userRepository.save(user)
        mailService.send(newEmail, "My GasStation Update Email", " Your verification code for update email: ${user.code.toString()}")

    }

    @Throws(
        UserIsNotFoundException::class, InvalidVerificationCodeException::class,
        InvalidEmailFormatException::class)
    override fun updateEmail(data: UserUpdateEmailRequest, userEmail: String) {
        val user: User = userRepository.findByEmail(userEmail)
            .orElseThrow{ UserIsNotFoundException("User with email $userEmail is not registered!") }

        if(!isEmailValid(data.newEmail)) throw InvalidEmailFormatException()
        if(data.code != user.code) throw InvalidVerificationCodeException()

        user.code = -1
        user.email = data.newEmail
        userRepository.save(user)

    }

    fun isEmailValid(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    fun genderFilterToEnum(genderId: Int): Gender{

        return if (genderId == 0) Gender.MALE
        else Gender.FEMALE
    }

    fun genderFilterFromEnum(gender: Gender): Int{

        return if (gender == Gender.MALE) 0
        else 1
    }

    fun isPhoneValid(phone: String): Boolean {
        return Pattern.compile(
            "^\\+[0-9]{10,13}\$"
        ).matcher(phone).matches()
    }

}