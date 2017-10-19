package gov.samhsa.c2s.ums.service.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Data
public class PatientDto {
    @NotEmpty
    private Long id;
    @NotEmpty
    private String mrn;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate birthDate;

    @NotEmpty
    private String genderCode;

    private String socialSecurityNumber;

    private List<AddressDto> addresses;

    private List<TelecomDto> telecoms;
    @Valid
    private Optional<List<IdentifierDto>> identifiers;

    private String relationship;

}
