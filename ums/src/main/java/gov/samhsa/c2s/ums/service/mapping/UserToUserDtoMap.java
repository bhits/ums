package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoMap extends PropertyMap<User, UserDto> {

    @Override
    protected void configure() {
        map().setId(source.getId());
        map().setFirstName(source.getDemographics().getFirstName());
        map().setMiddleName(source.getDemographics().getMiddleName());
        map().setLastName(source.getDemographics().getLastName());
        map().setBirthDate(source.getDemographics().getBirthDay());
        map().setGenderCode(source.getDemographics().getAdministrativeGenderCode().getCode());
        map().setSocialSecurityNumber(source.getDemographics().getSocialSecurityNumber());
        using(new TelecomListToTelecomDtoListConverter()).map(source.getDemographics().getTelecoms()).setTelecoms(null);
        using(new AddressListToAddressDtoListConverter()).map(source.getDemographics().getAddresses()).setAddresses(null);
        map().setLocale(source.getLocale().getCode());
        map().setMrn(source.getDemographics().getPatient().getMrn());
    }
}

