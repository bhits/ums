package gov.samhsa.c2s.ums.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MrnNotFoundException extends RuntimeException {
    public MrnNotFoundException() {
    }

    public MrnNotFoundException(String message) {
        super(message);
    }

    public MrnNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MrnNotFoundException(Throwable cause) {
        super(cause);
    }

    public MrnNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
