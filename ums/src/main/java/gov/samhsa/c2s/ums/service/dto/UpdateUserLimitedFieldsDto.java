package gov.samhsa.c2s.ums.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserLimitedFieldsDto {
    private String homePhone;
    private String homeEmail;
    private BaseAddressDto homeAddress;
    private String lastUpdatedBy;
}
