package com.jwtly.historicmarketdata.adapter.in.web;

import com.jwtly.historicmarketdata.adapter.in.web.dto.ApiError;
import com.jwtly.historicmarketdata.domain.exception.BrokerRequestException;
import com.jwtly.historicmarketdata.domain.exception.UnsupportedBrokerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class HistoricDataControllerAdvice {

    @ExceptionHandler(BrokerRequestException.class)
    public ResponseEntity<ApiError> handleBrokerError(BrokerRequestException ex) {
        log.error("Broker request failed: ({}) {}", ex.getMessage(), ex.getResponseBody());
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ApiError.fromBrokerException(ex));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Illegal argument", ex);
        return new ApiError(ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgument(MissingServletRequestParameterException ex) {
        log.error("Missing request parameter", ex);
        return new ApiError(ex.getMessage());
    }

    @ExceptionHandler(UnsupportedBrokerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleUnsupportedBroker(UnsupportedBrokerException ex) {
        log.error("Unsupported broker", ex);
        return new ApiError(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgument(MethodArgumentTypeMismatchException ex) {
        log.error("Method argument type mismatch", ex);
        return new ApiError(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception ex) {
        log.error("Internal server error", ex);
        return new ApiError("Internal server error");
    }
}