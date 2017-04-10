package gov.samhsa.c2s.ums.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.samhsa.c2s.ums.service.util.CustomJsonDateDeserializer;
import gov.samhsa.c2s.ums.service.util.CustomJsonDateSerializer;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
public class UserDto {
    @NotNull
    private Long id;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String firstName;

    @NotEmpty
    @Pattern(regexp = "^[\\w-]+(\\.[\\w-]+)*@([a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*?\\.[a-zA-Z]{2,6}|(\\d{1,3}\\.){3}\\d{1,3})(:\\d{4})?$")
    private String email;

    @Past
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    private Date birthDate;

    @NotEmpty
    private String genderCode;

    private String socialSecurityNumber;
    private String telephone;
    private String address;
    private String city;
    private String stateCode;
    private String zip;
}
