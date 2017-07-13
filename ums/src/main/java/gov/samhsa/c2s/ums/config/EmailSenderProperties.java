package gov.samhsa.c2s.ums.config;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "c2s.ums.email-sender")
@Data
public class EmailSenderProperties {

    @NotEmpty
    private String c2sUiRoute;

    @NotEmpty
    private String c2sUiVerificationRelativePath;

    @NotEmpty
    private String c2sUiVerificationEmailTokenArgName;

    @NotBlank
    private String c2sUiVerificationUserPreferredLocaleArgName;

    @NotEmpty
    private String brand;

    @NotNull
    @Min(0)
    private int emailTokenExpirationInDays;

    private List<String> disabledByRoles;

}