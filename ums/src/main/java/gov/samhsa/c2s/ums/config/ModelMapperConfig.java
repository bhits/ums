package gov.samhsa.c2s.ums.config;


import gov.samhsa.c2s.ums.domain.Role;
import gov.samhsa.c2s.ums.domain.Telecom;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.service.dto.GetUserResponseDto;
import gov.samhsa.c2s.ums.service.dto.TelecomDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

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
            map().setFirstName(source.getFirstName());
            map().setLastName(source.getLastName());
            map().setBirthDate(source.getBirthDay());
            map().setGenderCode(source.getAdministrativeGenderCode().getDisplayName());
            map().setSocialSecurityNumber(source.getSocialSecurityNumber());
        }
    }

    /**
     * Map User to GetUserResponseDto
     */
    @Component
    static class UserToGetUserResponseDtoMap extends PropertyMap<User, GetUserResponseDto> {

        @Override
        protected void configure() {
            map().setId(source.getId());
            map().setFirstName(source.getFirstName());
            map().setLastName(source.getLastName());
            map().setBirthDate(source.getBirthDay());
            map().setGenderCode(source.getAdministrativeGenderCode().getDisplayName());
            map().setSocialSecurityNumber(source.getSocialSecurityNumber());
            //.map().setAddress(source.getAddress());
            map().setTelecom(telecomListToTelecomDtoList(source.getTelecoms()));
            map().setLocale(source.getLocale().getCode());
            map().setRole(setRolesForUser(source.getRoles()));
        }
    }

    /**
     * Map Telecom to TelecomDto
     */
    @Component
    static class TelecomToTelecomDtoMap extends PropertyMap<Telecom, TelecomDto> {

        @Override
        protected void configure() {
            map().setSystem(source.getSystem());
            map().setValue(source.getValue());
        }
    }


    public static List<TelecomDto> telecomListToTelecomDtoList(List<Telecom> telecomList) {
        List<TelecomDto> telecomDtoList = new ArrayList<>();

        if (telecomList != null && telecomList.size() > 0) {

            for (Telecom tempTelecom : telecomList) {
                TelecomDto tempTelecomDto = new TelecomDto();
                tempTelecomDto.setValue(tempTelecom.getValue());
                tempTelecomDto.setSystem(tempTelecom.getSystem());
                telecomDtoList.add(tempTelecomDto);
            }
        }
        return telecomDtoList;
    }

    public static List<String> setRolesForUser(Set<Role> roleList){
        List<String> roleStringList = new ArrayList<>();

        if(roleList != null && roleList.size() >0){
            for (Role tempRole : roleList){
                roleStringList.add(tempRole.getName());
            }
        }
        return roleStringList;
    }

}
