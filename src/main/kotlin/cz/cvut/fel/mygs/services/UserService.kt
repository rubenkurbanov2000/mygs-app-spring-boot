package cz.cvut.fel.mygs.services

import cz.cvut.fel.mygs.auth.JwtResponse
import cz.cvut.fel.mygs.controllers.*
import cz.cvut.fel.mygs.models.User

interface UserService {
    fun attemptRegistration(userDetails: UserRegistrationRequest): User
    fun attemptAuthorization(userDetails: UserLogInRequest): JwtResponse
    fun attemptActivateVerificationCode(userDetails: UserVerificationRequest): JwtResponse
    fun setVerificationCodeForResetPassword(email: String)
    fun resetPasswordForUnauthorizedUser(userData: UserResetPasswordRequest)
    fun resetPasswordForAuthorizedUser(data: UserResetPasswordAuthRequest, userEmail: String)
    fun updatePhoneNumber(newPhoneData: UserUpdatePhoneRequest, userEmail: String)
    fun getVerificationCodeToChangeEmail(userEmail: String, newEmail: String)
    fun updateEmail(data: UserUpdateEmailRequest, userEmail: String)
}