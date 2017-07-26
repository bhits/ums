package gov.samhsa.c2s.ums.service.exception.checkedexceptions;

import java.io.IOException;

public class TempCacheFileException extends IOException {
    public TempCacheFileException() {
        super();
    }

    public TempCacheFileException(String message) {
        super(message);
    }
}
