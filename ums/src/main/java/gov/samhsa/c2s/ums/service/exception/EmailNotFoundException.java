package gov.samhsa.c2s.ums.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException() {
    }

    public EmailNotFoundException(String message) {
        super(message);
    }

    public EmailNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailNotFoundException(Throwable cause) {
        super(cause);
    }

    public EmailNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
