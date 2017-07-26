package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.exception.checkedexceptions.NoImageReaderForFileType;
import org.springframework.stereotype.Service;

import java.awt.Dimension;

@Service
public interface ImageProcessingService {
    Dimension getImageDimension(byte[] imageFileBytes, String fileExtension) throws NoImageReaderForFileType;
}
