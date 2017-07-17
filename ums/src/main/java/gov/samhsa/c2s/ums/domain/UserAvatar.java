package gov.samhsa.c2s.ums.domain;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class UserAvatar {
    @Id
    @GeneratedValue
    private Long id;

    @Lob
    @NotEmpty
    private byte[] fileContents;

    @NotEmpty
    private String fileName;

    @NotEmpty
    private String fileExtension;

    @NotNull
    @Min(1)
    private Long fileSizeBytes;

    @NotNull
    @Min(1)
    private Long fileWidthPixels;

    @NotNull
    @Min(1)
    private Long fileHeightPixels;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;
}
