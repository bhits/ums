package gov.samhsa.c2s.ums.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserLimitedFieldsDto {
    private String homePhone;
    private String homeEmail;
    private BaseAddressDto homeAddress;
}
