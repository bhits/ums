package gov.samhsa.c2s.ums.service.dto;

import org.hibernate.validator.constraints.NotEmpty;

public class PatientDto {
    @NotEmpty
    private Long id;
    @NotEmpty
    private String mrn;
    private UserDto user;
}
