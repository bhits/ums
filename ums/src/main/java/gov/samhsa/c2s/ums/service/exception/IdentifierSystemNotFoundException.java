package gov.samhsa.c2s.ums.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IdentifierSystemNotFoundException extends RuntimeException {
    public IdentifierSystemNotFoundException() {
    }

    public IdentifierSystemNotFoundException(String message) {
        super(message);
    }

    public IdentifierSystemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdentifierSystemNotFoundException(Throwable cause) {
        super(cause);
    }

    public IdentifierSystemNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
