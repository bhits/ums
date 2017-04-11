package gov.samhsa.c2s.ums.config;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "c2s.ums")
@Data
public class UmsProperties {

    private Ssn ssn;
    private Gender gender;
    private Mrn mrn;

    @Data
    public static class Identifier {
        @NotNull
        private String codeSystem;

        @NotEmpty
        private String codeSystemOID;

        @NotEmpty
        private String displayName;
    }

    @Data
    public static class Mrn extends Identifier{ }

    @Data
    public static class Ssn extends Identifier{ }

    @Data
    public static class Gender extends Identifier{ }

}
