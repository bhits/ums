package gov.samhsa.c2s.ums.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvatarBytesAndMetaDto {
    private byte[] fileContents;
    private String fileExtension;
    private String fileName;
    private Long fileSizeBytes;
}
