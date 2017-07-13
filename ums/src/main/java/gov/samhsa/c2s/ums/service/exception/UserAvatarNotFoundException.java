package gov.samhsa.c2s.ums.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserAvatarNotFoundException extends RuntimeException {
    public UserAvatarNotFoundException() {}

    public UserAvatarNotFoundException(String message) {
        super(message);
    }

    public UserAvatarNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAvatarNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserAvatarNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
