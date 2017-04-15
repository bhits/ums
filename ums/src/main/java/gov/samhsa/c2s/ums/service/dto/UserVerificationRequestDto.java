package gov.samhsa.c2s.ums.service.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class UserVerificationRequestDto {

    @NotEmpty
    private String emailToken;

    private String verificationCode;

    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
}
