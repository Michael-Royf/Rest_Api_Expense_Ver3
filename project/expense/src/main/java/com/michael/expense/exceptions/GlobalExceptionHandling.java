package com.michael.expense.exceptions;

import com.michael.expense.exceptions.payload.EmailExistException;
import com.michael.expense.exceptions.payload.TokenException;
import com.michael.expense.exceptions.payload.UserNotFoundException;
import com.michael.expense.exceptions.payload.UsernameExistException;
import com.michael.expense.payload.response.ErrorResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandling extends ResponseEntityExceptionHandler {

    private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration";
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request";
    private static final String INCORRECT_CREDENTIALS = "Username / password incorrect. Please try again";
    private static final String ACCOUNT_DISABLED = "Your account has been disabled. If this is an error, please contact administration";
    private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
    private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";
    public static final String ERROR_PATH = "/error";


    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponseMessage> iOException(IOException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseMessage> handleMethodUserNotFound(UserNotFoundException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseMessage> handleMethodGlobalException(Exception ex) {
        return createHttpResponse(BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<ErrorResponseMessage> usernameExistException(UsernameExistException exception) {
        return createHttpResponse(CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<ErrorResponseMessage> emailExistException(EmailExistException exception) {
        return createHttpResponse(CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseMessage> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        return createHttpResponse(BAD_REQUEST, ex.getMessage());
    }


    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ErrorResponseMessage> handleMethodArgumentTypeMismatchException(TokenException ex) {
        return createHttpResponse(BAD_REQUEST, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("timestamp", new Date());
        body.put("statusCode", BAD_REQUEST.value());
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        body.put("messages", errors);
        return new ResponseEntity<Object>(body, BAD_REQUEST);
    }


    private ResponseEntity<ErrorResponseMessage> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new ErrorResponseMessage(
                        httpStatus.value(),
                        httpStatus,
                        message),
                httpStatus);
    }


}
