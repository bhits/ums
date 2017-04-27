package gov.samhsa.c2s.ums.config;


import gov.samhsa.c2s.ums.domain.Address;
import gov.samhsa.c2s.ums.domain.Demographics;
import gov.samhsa.c2s.ums.domain.Role;
import gov.samhsa.c2s.ums.domain.Telecom;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.service.dto.AddressDto;
import gov.samhsa.c2s.ums.service.dto.RoleDto;
import gov.samhsa.c2s.ums.service.dto.TelecomDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Configuration
public class ModelMapperConfig {

    /**
     * Initializes {@link ModelMapper} with available {@link PropertyMap} instances.
     *
     * @param propertyMaps List of PropertyMap Implementors
     * @return ModelMapper
     */
    @Bean
    public ModelMapper modelMapper(List<PropertyMap> propertyMaps) {
        final ModelMapper modelMapper = new ModelMapper();
        propertyMaps.stream().filter(Objects::nonNull).forEach(modelMapper::addMappings);
        return modelMapper;
    }

    /**
     * Map User to UserDto
     */
    @Component
    static class UserToUserDtoMap extends PropertyMap<User, UserDto> {

        @Override
        protected void configure() {
            map().setId(source.getId());
            map().setFirstName(source.getDemographics().getFirstName());
            map().setMiddleName(source.getDemographics().getMiddleName());
            map().setLastName(source.getDemographics().getLastName());
            map().setBirthDate(source.getDemographics().getBirthDay());
            map().setGenderCode(source.getDemographics().getAdministrativeGenderCode().getDisplayName());
            map().setSocialSecurityNumber(source.getDemographics().getSocialSecurityNumber());
            map().setTelecoms(mapTelecomListToTelecomDtoList(source.getDemographics().getTelecoms()));
            map().setAddresses(mapAddressListToAddressDtoList(source.getDemographics().getAddresses()));
            map().setRoles(mapRoleListToRoleDtoList(source.getRoles()));
            map().setLocale(source.getLocale().getCode());
        }
    }

    public static List<TelecomDto> mapTelecomListToTelecomDtoList(List<Telecom> telecomList) {
        List<TelecomDto> telecomDtoList = new ArrayList<>();

        if (telecomList != null && telecomList.size() > 0) {

            for (Telecom tempTelecom : telecomList) {
                TelecomDto tempTelecomDto = new TelecomDto();
                tempTelecomDto.setValue(tempTelecom.getValue());
                tempTelecomDto.setSystem(tempTelecom.getSystem().toString());
                tempTelecomDto.setUse(tempTelecom.getUse().toString());
                telecomDtoList.add(tempTelecomDto);
            }
        }
        return telecomDtoList;
    }

    public static List<AddressDto> mapAddressListToAddressDtoList(List<Address> addressList) {
        List<AddressDto> addressDtoList = new ArrayList<>();

        if (addressList != null && addressList.size() > 0) {

            for (Address tempAddress : addressList) {
                AddressDto tempAddressDto = new AddressDto();
                tempAddressDto.setLine1(tempAddress.getLine1());
                tempAddressDto.setLine2(tempAddress.getLine2());
                tempAddressDto.setCity(tempAddress.getCity());
                if (tempAddress.getCountryCode() != null)
                    tempAddressDto.setCountryCode(tempAddress.getCountryCode().getCode());
                if (tempAddress.getStateCode() != null)
                    tempAddressDto.setStateCode(tempAddress.getStateCode().getCode());
                tempAddressDto.setUse(tempAddress.getUse().toString());
                tempAddressDto.setPostalCode(tempAddress.getPostalCode());
                addressDtoList.add(tempAddressDto);
            }
        }
        return addressDtoList;
    }

    public static AddressDto mapAddressToAddressDto(Address address) {
        AddressDto tempAddressDto = new AddressDto();
        tempAddressDto.setLine1(address.getLine1());
        tempAddressDto.setLine2(address.getLine2());
        tempAddressDto.setCity(address.getCity());
        if (address.getCountryCode() != null)
            tempAddressDto.setCountryCode(address.getCountryCode().getDisplayName());
        if (address.getStateCode() != null)
            tempAddressDto.setStateCode(address.getStateCode().getCode());
        tempAddressDto.setPostalCode(address.getPostalCode());
        return tempAddressDto;

    }


    public static List<RoleDto> mapRoleListToRoleDtoList(Set<Role> roles) {

        List<RoleDto> roleDtoList = new ArrayList<>();
        if (roles != null && roles.size() > 0) {
            roles.stream().forEach(role ->
                    roleDtoList.add(RoleDto.builder().code(role.getCode()).name(role.getName()).build())
            );
        }
        return roleDtoList;
    }

}
