package gov.samhsa.c2s.ums.service.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.time.LocalDate;

@Data
public class PatientDto {
    @NotEmpty
    private Long id;
    @NotEmpty
    private String mrn;
    private String firstName;
    private String lastName;
    private String email;

    private LocalDate birthDate;

    private String genderCode;

}
