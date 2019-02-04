package org.mtgpeasant.tournaments.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BadStateException extends RuntimeException {
    public BadStateException(String message) {
        super(message);
    }

    public BadStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
