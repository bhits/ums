package gov.samhsa.c2s.ums.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SsnSystemNotFoundException extends RuntimeException {
    public SsnSystemNotFoundException() {
    }

    public SsnSystemNotFoundException(String message) {
        super(message);
    }

    public SsnSystemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SsnSystemNotFoundException(Throwable cause) {
        super(cause);
    }

    public SsnSystemNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
