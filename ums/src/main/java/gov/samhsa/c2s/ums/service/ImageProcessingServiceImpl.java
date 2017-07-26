package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.exception.checkedexceptions.NoImageReaderForFileTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;

@Service
@Slf4j
public class ImageProcessingServiceImpl implements ImageProcessingService {
    @Override
    public Dimension getImageDimension(byte[] imageFileBytes, String fileExtension) throws NoImageReaderForFileTypeException {
        Dimension imageDimension = null;
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(fileExtension);

        byte[] imageFileDataBytes = extractDataPartOfDataURI(imageFileBytes);

        // Loop through all ImageReader found for fileExtension and try each one
        while(iter.hasNext()) {
            ImageReader reader = iter.next();
            try (ByteArrayInputStream imgByteAryStream = new ByteArrayInputStream(imageFileDataBytes);
                 ImageInputStream stream = new MemoryCacheImageInputStream(imgByteAryStream)) {

                reader.setInput(stream);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                imageDimension = new Dimension(width, height);
            } catch (IOException e) {
                log.warn("Error reading image file from byte array", e);
            } finally {
                reader.dispose();  // ImageReader must be disposed of in finally block because try-with-resources only works with classes that implement Closeable
            }
        }

        if (imageDimension == null) {
            log.error("No ImageReader compatible with the following file extension type could be found: " + fileExtension);
            throw new NoImageReaderForFileTypeException("Not a known image file extension: " + fileExtension);
        }

        return imageDimension;
    }

    /**
     * Extract only the data part of a base64 encoded Data URI represented as a byte[]
     * <p>
     * Removes the data type and base64 label from a base64 encoded Data URI, decodes the data part, and returns just the base64 decoded data part
     *
     * @param inDataURI - the full Data URI from which to extract the data part
     * @return a byte[] containing only the data part of the Data URI
     */
    private byte[] extractDataPartOfDataURI(byte[] inDataURI) {
        // Extract only the image data part of the Data URI
        String avatarFileBytesAsString = new String(inDataURI);
        int indexOfDataBytesStart = avatarFileBytesAsString.indexOf(";base64,") + 8;  // Add 8 to the returned index value to offset from the start of the ";base64," substring
        byte[] avatarFileProcessedBytes = avatarFileBytesAsString.substring(indexOfDataBytesStart).getBytes();
        return Base64.getDecoder().decode(avatarFileProcessedBytes);
    }
}
