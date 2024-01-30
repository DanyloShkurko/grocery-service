package org.example.exception;

import org.example.external.model.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleProductServiceException(CustomException exception){
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .errorCode(exception.getErrorCode())
                        .errorMessage(exception.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build(),
                HttpStatus.NOT_FOUND);
    }
}