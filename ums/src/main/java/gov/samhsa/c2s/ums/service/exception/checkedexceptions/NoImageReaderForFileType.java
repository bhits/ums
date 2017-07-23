package gov.samhsa.c2s.ums.service.exception.checkedexceptions;

import java.io.IOException;

public class NoImageReaderForFileType extends IOException {
    public NoImageReaderForFileType() {
        super();
    }

    public NoImageReaderForFileType(String message) {
        super(message);
    }
}
