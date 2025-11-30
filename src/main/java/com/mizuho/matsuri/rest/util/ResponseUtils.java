package com.mizuho.matsuri.rest.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.ResponseEntity.status;

public class ResponseUtils {
    /**
     Private constructor to prevent instantiation
     */
    private ResponseUtils() {};


    /**
     * Build an OK response object
     * @return OK response
     */
    public static ResponseEntity<String> ofOKResponse() {
        return ResponseEntity.ok("Request completed succesfully");
    }

    /**
     * Build an expectation failed response with the passed error message.
     * @param message error message
     * @return EXPECTATION_FAILED response
     */
    public static ResponseEntity<String> ofFailedResponse(String message) {
        return expFailed().body(message);
    }

    private static ResponseEntity.BodyBuilder expFailed() {
        return status(HttpStatus.EXPECTATION_FAILED);
    }

}
