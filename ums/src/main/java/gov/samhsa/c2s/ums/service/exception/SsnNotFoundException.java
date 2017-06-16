package gov.samhsa.c2s.ums.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SsnNotFoundException extends RuntimeException {
    public SsnNotFoundException() {
    }

    public SsnNotFoundException(String message) {
        super(message);
    }

    public SsnNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SsnNotFoundException(Throwable cause) {
        super(cause);
    }

    public SsnNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
