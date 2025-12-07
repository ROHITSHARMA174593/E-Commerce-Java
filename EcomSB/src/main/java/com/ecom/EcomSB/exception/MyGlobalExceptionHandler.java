package com.ecom.EcomSB.exception;


import com.ecom.EcomSB.payload.APIResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice

public class MyGlobalExceptionHandler {

    // @ExceptionHandler(Exception.class)   // if you don't know the which type of error you will get than you can use this simple line
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException err) {
        Map<String, String> response = new HashMap<>();
        err.getBindingResult().getFieldErrors().forEach(e -> {
            response.put(e.getField(), e.getDefaultMessage()); // ye line postman per show karegi ki (categoryName : "Must Not Be Blank")  ab vha per bad request, internal server error ye sab nahi aayenge seedhe seedhe message aayega user ke liye
        });
        return new ResponseEntity<Map<String, String>>(response, HttpStatus.BAD_REQUEST); // yaha per Response Entity add kiya hai kyuki response me hume 200 status code mil rha tha empty CategoryName per bhi so which is not good that's why we use it
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException err){
        String message = err.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    //todo : API Exception (For Duplicate Data || User cannot insert duplicate data)
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException err){
        String message = err.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

}
