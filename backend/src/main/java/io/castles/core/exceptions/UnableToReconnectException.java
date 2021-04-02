package io.castles.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UnableToReconnectException extends Exception {
    public UnableToReconnectException(String message) {
        super(message);
    }
}
