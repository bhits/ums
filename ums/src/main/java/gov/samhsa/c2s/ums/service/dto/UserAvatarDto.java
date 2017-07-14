package gov.samhsa.c2s.ums.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAvatarDto {
    @NotNull
    private Long id;

    @NotEmpty
    private byte[] fileContents;

    @NotEmpty
    private String fileName;

    @NotEmpty
    private String fileExtension;

    @NotNull
    private Long fileSizeBytes;

    @NotNull
    private Long fileWidthPixels;

    @NotNull
    private Long fileHeightPixels;

    @NotNull
    private Long userId;
}
