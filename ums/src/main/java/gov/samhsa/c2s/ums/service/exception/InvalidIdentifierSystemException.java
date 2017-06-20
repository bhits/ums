package gov.samhsa.c2s.ums.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidIdentifierSystemException extends RuntimeException {
    public InvalidIdentifierSystemException() {
    }

    public InvalidIdentifierSystemException(String message) {
        super(message);
    }

    public InvalidIdentifierSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidIdentifierSystemException(Throwable cause) {
        super(cause);
    }

    public InvalidIdentifierSystemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
