package gov.samhsa.c2s.ums.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAvatarInputException extends RuntimeException {
    public InvalidAvatarInputException() {}

    public InvalidAvatarInputException(String message) {
        super(message);
    }

    public InvalidAvatarInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAvatarInputException(Throwable cause) {
        super(cause);
    }

    public InvalidAvatarInputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
