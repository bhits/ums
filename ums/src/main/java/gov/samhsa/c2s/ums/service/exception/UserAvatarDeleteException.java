package gov.samhsa.c2s.ums.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UserAvatarDeleteException extends RuntimeException {
    public UserAvatarDeleteException() {}

    public UserAvatarDeleteException(String message) {
        super(message);
    }

    public UserAvatarDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAvatarDeleteException(Throwable cause) {
        super(cause);
    }

    public UserAvatarDeleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
