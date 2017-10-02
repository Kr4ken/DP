package com.kr4ken.dp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TaskTypeNotFoundException extends RuntimeException {

        public TaskTypeNotFoundException(String interestId) {
                super("Could not find task type '" + interestId + "'.");
        }
}
