package gov.samhsa.c2s.ums.config;


import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Configuration
public class ModelMapperConfig {

    /**
     * Initializes {@link ModelMapper} with available {@link PropertyMap} instances.
     *
     * @param propertyMaps
     * @return
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
            map().setEmail(source.getEmail());
            map().setBirthDate(source.getBirthDay());
            map().setGenderCode(source.getAdministrativeGenderCode().getDisplayName());
            map().setSocialSecurityNumber(source.getSocialSecurityNumber());
            map().setTelephone(source.getTelecom().getTelephone());
            map().setAddress(source.getAddress().getStreetAddressLine());
            map().setCity(source.getAddress().getCity());
            map().setStateCode(source.getAddress().getStateCode().getDisplayName());
            map().setZip(source.getAddress().getPostalCode());
        }
    }

}
