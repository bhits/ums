package gov.samhsa.c2s.ums.config;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "c2s.ums")
@Data
public class UmsProperties {

    private Ssn ssn;
    private Gender gender;
    private Mrn mrn;
    private Pagination pagination;
    private Fhir fhir;

    @Data
    public static class Identifier {
        @NotBlank
        private String codeSystem;

        @NotBlank
        private String codeSystemOID;

        @NotBlank
        private String displayName;
    }

    @Data
    public static class Mrn extends Identifier{
        @NotBlank
        private String prefix;

        @NotBlank
        private int length;
    }

    @Data
    public static class Ssn extends Identifier{ }

    @Data
    public static class Gender extends Identifier{ }

    @Data
    public static class Pagination{
            @Min(1)
            @NotNull
            private int defaultSize;

            @NotNull
            private int maxSize;
    }

    @Data
    public static class Fhir{

        private Publish publish;

        @Data
        public static class Publish {
            @NotBlank
            private boolean enabled;
            @NotBlank
            private String serverUrl;
            @NotBlank
            private String clientSocketTimeoutInMs;

        }
    }
}
