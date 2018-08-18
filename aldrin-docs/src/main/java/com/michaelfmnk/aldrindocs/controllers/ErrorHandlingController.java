package com.michaelfmnk.aldrindocs.controllers;

import com.michaelfmnk.aldrindocs.dtos.ErrorDetailDto;
import com.michaelfmnk.aldrindocs.utils.TimeProvider;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;


@CommonsLog
@RestController
@ControllerAdvice
@AllArgsConstructor
public class ErrorHandlingController extends ResponseEntityExceptionHandler {

    private final TimeProvider timeProvider;

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDetailDto exceptionHandler(EntityNotFoundException e) {
        ErrorDetailDto errorDetailDto = ErrorDetailDto.builder()
                .status(HttpStatus.NOT_FOUND)
                .cause(e)
                .timeStamp(timeProvider.getDate())
                .build();
        return errorDetailDto;
    }

}
