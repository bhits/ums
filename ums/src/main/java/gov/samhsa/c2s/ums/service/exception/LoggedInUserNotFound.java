package gov.samhsa.c2s.ums.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class LoggedInUserNotFound extends RuntimeException {

    public LoggedInUserNotFound() {

    }

    public LoggedInUserNotFound(String message) {
        super(message);
    }

    public LoggedInUserNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public LoggedInUserNotFound(Throwable cause) {
        super(cause);
    }

    public LoggedInUserNotFound(String message, Throwable cause, boolean enableSuppression, boolean writeableStackTrace) {
        super(message, cause, enableSuppression, writeableStackTrace);
    }
}
