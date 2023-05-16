package cz.cvut.fel.mygs.exceptios

import cz.cvut.fel.mygs.variables.ResponseConstants
import org.springframework.http.HttpStatus

open class ControllerException(message: String, val code: String, val status: HttpStatus = HttpStatus.UNPROCESSABLE_ENTITY) : RuntimeException(message)

class DenyAccessException(message: String = "User credential invalid!") : ControllerException(message, ResponseConstants.INVALID_CREDENTIAL.value)

//USER EXCEPTIONS
class UserAlreadyRegisteredException(message: String = "User with this email is already registered!") : ControllerException(message, ResponseConstants.EMAIL_ALREADY_EXIST.value)
class InvalidEmailFormatException(message: String = "Invalid email format!") : ControllerException(message, ResponseConstants.INVALID_EMAIL.value)
class InvalidPhoneFormatException(message: String = "Invalid phone format!") : ControllerException(message, ResponseConstants.INVALID_PHONE.value)
class InvalidUserDataException(message: String = "Invalid user data! Check if name or surname is not empty and dob is correct.") : ControllerException(message, ResponseConstants.INVALID_USER_DATA.value)
class ShortPasswordException(message: String = "Password must be at least 5 characters!") : ControllerException(message, ResponseConstants.SHORT_PASSWORD.value)
class UserIsNotFoundException(message: String = "User with this email is not registered!") : ControllerException(message, ResponseConstants.USER_IS_NOT_EXIST.value)
class UnsuccessfulAuthorizationException(message: String = "Authorization failed. Wrong login or password!") : ControllerException(message, ResponseConstants.AUTHORIZATION_FAILED.value)
class UserHasAlreadyVerifiedException(message: String = "The user has already been verified!") : ControllerException(message, ResponseConstants.USER_ALREADY_VERIFIED.value)
class InvalidVerificationCodeException(message: String = "Invalid confirmation code!") : ControllerException(message, ResponseConstants.INVALID_CODE.value)
class VerificationCodeNotConfirmedExceptions(message: String = "The user has not verified his email!") : ControllerException(message, ResponseConstants.CODE_NOT_CONFIRMED.value)
class PasswordMismatchExceptions(message: String = "Password mismatch!") : ControllerException(message, ResponseConstants.PASSWORD_MISMATCH.value)
class InvalidPasswordExceptions(message: String = "Wrong current password!") : ControllerException(message, ResponseConstants.INVALID_PASSWORD.value)

//CAR EXCEPTIONS
class EmptyCarBrandNameExceptions(message: String = "The car brand cannot be empty!") : ControllerException(message, ResponseConstants.EMPTY_BRAND.value)
class EmptyCarNumberExceptions(message: String = "The car number cannot be empty!") : ControllerException(message, ResponseConstants.EMPTY_NUMBER.value)
class UnknownFuelTypeExceptions(message: String = "Unknown type of gasoline!") : ControllerException(message, ResponseConstants.UNKNOWN_GASOLINE_TYPE.value)
class CarIsNotFoundExceptions(message: String = "The car cannot be deleted because it does not belong to this user!") : ControllerException(message, ResponseConstants.CAR_NOT_FOUND.value)

//CARD EXCEPTIONS
class EmptyCardNumberExceptions(message: String = "The card number cannot be empty!") : ControllerException(message, ResponseConstants.CARD_NUMBER_EMPTY.value)
class WrongCardNumberFormatExceptions(message: String = "These card numbers must consist only of numbers and the number must not be less or more than 16 characters!") : ControllerException(message, ResponseConstants.WRONG_CARD_NUMBER_FORMAT.value)
class FailedToRegisterCardException(message: String = "Failed to register card!") : ControllerException(message, ResponseConstants.FAIL_TO_SAVE_CARD.value)
class CardExpiredException(message: String = "The card has expired!") : ControllerException(message, ResponseConstants.CARD_EXPIRED.value)
class WrongCvvCodeExceptions(message: String = "The cvv code has wrong format!") : ControllerException(message, ResponseConstants.WRONG_CVV_FORMAT.value)
class CardIsNotFoundExceptions(message: String = "The card cannot be deleted because it does not belong to this user!") : ControllerException(message, ResponseConstants.CARD_NOT_FOUND.value)

//ORDER EXCEPTIONS
class WrongAmountOfFillingUnitException(message: String = "Wrong amount of filling unit!") : ControllerException(message, ResponseConstants.WRONG_AMOUNT.value)
class OrderIsNotFoundExceptions(message: String = "The order does not exist!") : ControllerException(message, ResponseConstants.ORDER_NOT_FOUND.value)

//GAS STATION EXCEPTIONS
class GasStationIsNotFoundException(message: String = "Gas station with given id was not found!") : ControllerException(message, ResponseConstants.GAS_STATION_NOT_FOUND.value)
class GasStationNameIsEmptyException(message: String = "Gas station name cannot be empty!") : ControllerException(message, ResponseConstants.EMPTY_NAME.value)
class GasStationServicesIsEmptyException(message: String = "Gas station services cannot be empty!") : ControllerException(message, ResponseConstants.EMPTY_SERVICES.value)
class GasStationPaymentMethodsIsEmptyException(message: String = "Gas station payment methods cannot be empty!") : ControllerException(message, ResponseConstants.EMPTY_PAYMENT_METHODS.value)
class GasStationDoesNotBelongToManagerException(message: String = "This gas station does not belong to this manager and cannot be edited by him!") : ControllerException(message, ResponseConstants.WRONG_MANAGER.value)
class RefuelingStandAlreadyExistException(message: String = "A dispenser with this number is already registered!") : ControllerException(message, ResponseConstants.REFUELING_STAND_ALREADY_EXIST.value)

//OFFER EXCEPTIONS
class OfferIsNotFoundException(message: String = "Offer with given id was not found!") : ControllerException(message, ResponseConstants.OFFER_NOT_FOUND.value)


//FUEL EXCEPTIONS
class FuelIsNotFoundException(message: String = "Fuel with given id was not found!") : ControllerException(message, ResponseConstants.FUEL_NOT_FOUND.value)

//FEEDBACK EXCEPTIONS
class ContentOfFeedbackCanNotBeEmptyException(message: String = "Feedback content cannot be empty!") : ControllerException(message, ResponseConstants.EMPTY_CONTENT.value)
class TitleOfFeedbackCanNotBeEmptyException(message: String = "Feedback title cannot be empty!") : ControllerException(message, ResponseConstants.EMPTY_TITLE.value)
class TitleOfFeedbackIsSoLongException(message: String = "The number of characters for the feedback title cannot be more than 20 characters and less than 2!") : ControllerException(message, ResponseConstants.WRONG_TITLE_LENGTH.value)





