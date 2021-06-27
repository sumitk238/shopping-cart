package com.maersk.shoppingcart.exception.handler;

import com.maersk.shoppingcart.dto.ProblemDetails;
import com.maersk.shoppingcart.exception.DuplicateDataException;
import com.maersk.shoppingcart.exception.InvalidDataException;
import com.maersk.shoppingcart.rest.ShoppingCartEndpoint;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {ShoppingCartEndpoint.class})
public class ShoppingCartExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartExceptionHandler.class);
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Something went wrong !! Please try later !!";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ResponseEntity<ProblemDetails> handleException(InvalidDataException e, HttpServletRequest request) {
        logRequestAndExceptionDetails(e, request);
        ProblemDetails problemDetails = new ProblemDetails();
        problemDetails.setReason(e.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        return new ResponseEntity<>(problemDetails, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ResponseEntity<ProblemDetails> handleException(DuplicateDataException e, HttpServletRequest request) {
        logRequestAndExceptionDetails(e, request);
        ProblemDetails problemDetails = new ProblemDetails();
        problemDetails.setReason(e.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        return new ResponseEntity<>(problemDetails, headers, HttpStatus.BAD_REQUEST);
    }

    /**
     * Catches all exception during rest api processing and returns a meaningful response to user
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    ResponseEntity<ProblemDetails> handleException(Exception e, HttpServletRequest request) {
        logRequestAndExceptionDetails(e, request);
        ProblemDetails problemDetails = new ProblemDetails();
        problemDetails.setReason(INTERNAL_SERVER_ERROR_MESSAGE);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        return new ResponseEntity<>(problemDetails, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logRequestAndExceptionDetails(Exception e, HttpServletRequest request) {
        logger.error("Error processing request: {}, method={}, path={}", e.getMessage(),
                request.getMethod(), request.getRequestURI(), e);
    }
}
