package gov.samhsa.c2s.ums.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotNull
    private Long id;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String firstName;

    @Past
    @NotNull
    private LocalDate birthDate;

    @NotEmpty
    private String genderCode;

    private String socialSecurityNumber;

    private AddressDto address;

    private List<TelecomDto> telecom;
}
