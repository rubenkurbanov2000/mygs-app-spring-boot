package cz.cvut.fel.mygs.components

import cz.cvut.fel.mygs.exceptios.ControllerException
import cz.cvut.fel.mygs.variables.ErrorResponse

import cz.cvut.fel.mygs.variables.ResponseConstants
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerErrorHandler {

    companion object {
        private val LOG = LoggerFactory.getLogger(ControllerErrorHandler::class.java)
    }

    @ExceptionHandler(Exception::class)
    fun unexpected(ex: Exception): ResponseEntity<ErrorResponse> {
        LOG.error(ex.message, ex)
        val response = ResponseEntity(ErrorResponse(ResponseConstants.UNKNOWN.value, "Unexpected an error"), HttpStatus.INTERNAL_SERVER_ERROR)
        LOG.error("unexpectedException: ${response.body.toString()}")
        return response
    }

    @ExceptionHandler(ControllerException::class)
    fun conversationIdInvalidException(controllerException: ControllerException):
            ResponseEntity<ErrorResponse> {
        val res = ErrorResponse(controllerException.code, controllerException.message ?: "Empty message")
        val response = ResponseEntity.status(controllerException.status).body(res)
        LOG.error("Code: ${controllerException.code} message: ${controllerException.message} status: ${controllerException.status} ${response.body.toString()}")
        return response
    }
}