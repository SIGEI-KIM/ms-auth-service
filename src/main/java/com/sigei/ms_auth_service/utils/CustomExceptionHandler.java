package com.sigei.ms_auth_service.utils;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception ex){

        ProblemDetail errorDetail = null;

        if (ex instanceof BadCredentialsException){
            errorDetail = ProblemDetail
                    .forStatusAndDetail(HttpStatusCode
                            .valueOf(401), ex.getMessage());
            errorDetail.setProperty("access_Denied_reason","Authentication Failure");

    }
        return errorDetail;
    }
}
