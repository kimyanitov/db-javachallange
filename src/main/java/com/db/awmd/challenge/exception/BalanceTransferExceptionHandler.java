package com.db.awmd.challenge.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class BalanceTransferExceptionHandler extends RuntimeException {

    @ExceptionHandler(BalanceTransferException.class)
    public ResponseEntity handle(BalanceTransferException bte) {
        log.error(bte.getMessage(), bte);
        return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
    }
}
