package gov.samhsa.c2s.ums.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto extends BaseAddressDto {
    public AddressDto(BaseAddressDto baseAddressDto, String use) {
        super(
                baseAddressDto.getLine1(),
                baseAddressDto.getLine2(),
                baseAddressDto.getCity(),
                baseAddressDto.getStateCode(),
                baseAddressDto.getPostalCode(),
                baseAddressDto.getCountryCode()
        );

        this.use = use;
    }

    // copy constructor
    public AddressDto(AddressDto addressDto) {
        super(
                addressDto.getLine1(),
                addressDto.getLine2(),
                addressDto.getCity(),
                addressDto.getStateCode(),
                addressDto.getPostalCode(),
                addressDto.getCountryCode()
        );

        this.use = addressDto.getUse();
    }

    private String use;
}
