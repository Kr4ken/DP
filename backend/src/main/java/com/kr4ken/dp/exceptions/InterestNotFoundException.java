package com.kr4ken.dp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InterestNotFoundException extends RuntimeException {

    public InterestNotFoundException(String interestId) {
        super("Could not find interest '" + interestId + "'.");
    }
}
