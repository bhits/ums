package gov.samhsa.c2s.ums.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UserAvatarSaveException extends RuntimeException {
    public UserAvatarSaveException() {}

    public UserAvatarSaveException(String message) {
        super(message);
    }

    public UserAvatarSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAvatarSaveException(Throwable cause) {
        super(cause);
    }

    public UserAvatarSaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
