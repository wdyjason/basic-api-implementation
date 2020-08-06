package com.thoughtworks.rslist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class GlobalExceptionHandler {
    public static final int INVAILD_FOR_USER = 0;
    public static final int INVAILD_FOR_RSEVENT = 1;
    public static final int OTHER_EXCEPTION = 2;

    public static ResponseEntity globalExHandle(Exception ex, int condition) {
        CommonError commonError = new CommonError();
        if (ex instanceof OutOfIndexException) {
            commonError.setError(ex.getMessage());
        }
        if (ex instanceof MethodArgumentNotValidException) {
            if (condition == INVAILD_FOR_USER) commonError.setError("invalid user");
            if (condition == INVAILD_FOR_RSEVENT) commonError.setError("invalid param");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commonError);
    }
}
