package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.domain.Address;
import gov.samhsa.c2s.ums.service.dto.AddressDto;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AddressListToAddressDtoListConverter extends AbstractConverter<List<Address>, List<AddressDto>> {

    @Override
    protected List<AddressDto> convert(List<Address> source) {
        List<AddressDto> addressDtoList = new ArrayList<>();

        if (source != null && source.size() > 0) {
            for (Address tempAddress : source) {
                AddressDto tempAddressDto = new AddressDto();
                tempAddressDto.setLine1(tempAddress.getLine1());
                tempAddressDto.setLine2(tempAddress.getLine2());
                tempAddressDto.setCity(tempAddress.getCity());
                if (tempAddress.getCountryCode() != null)
                    tempAddressDto.setCountryCode(tempAddress.getCountryCode().getCode());
                if (tempAddress.getStateCode() != null)
                    tempAddressDto.setStateCode(tempAddress.getStateCode().getCode());
                if (tempAddress.getUse() != null)
                    tempAddressDto.setUse(tempAddress.getUse().toString());
                tempAddressDto.setPostalCode(tempAddress.getPostalCode());
                addressDtoList.add(tempAddressDto);
            }
        }
        return addressDtoList;
    }
}
