package gov.samhsa.c2s.ums.service.exception.checkedexceptions;

import java.io.IOException;

public class NoImageReaderForFileTypeException extends IOException {
    public NoImageReaderForFileTypeException() {
        super();
    }

    public NoImageReaderForFileTypeException(String message) {
        super(message);
    }
}
