package com.miniproject.productservice.customExceptionHandler;

public class ResoureNotFoundException extends RuntimeException{

    public ResoureNotFoundException(String message) {
        super(message);
    }
}
