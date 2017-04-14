package gov.samhsa.c2s.ums.config;


import gov.samhsa.c2s.ums.domain.Address;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.reference.*;
import gov.samhsa.c2s.ums.service.dto.AddressDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.modelmapper.AbstractConverter;
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
         //   map().setEmail(source.getEmail());
            map().setBirthDate(source.getBirthDay());
            map().setGenderCode(source.getAdministrativeGenderCode().getDisplayName());
            map().setSocialSecurityNumber(source.getSocialSecurityNumber());
           // map().setTelephone(source.getTelecom().getTelephone());
/*            map().setAddress(source.getAddress().getStreetAddressLine());
            map().setCity(source.getAddress().getCity());
            map().setStateCode(source.getAddress().getStateCode().getDisplayName());
            map().setZip(source.getAddress().getPostalCode());*/
        }
    }

    @Component
    public static class AdressDtoToAddress extends  PropertyMap<AddressDto, Address>{

        private final StateCodeConverter stateCodeConverter;
        private final CountryCodeConverter countryCodeConverter;
        @Autowired
        AdressDtoToAddress(StateCodeConverter stateCodeConverter, CountryCodeConverter countryCodeConverter) {
            this.stateCodeConverter = stateCodeConverter;
            this.countryCodeConverter = countryCodeConverter;
        }

        @Override
        protected void configure() {
             using(stateCodeConverter).map(source).setStateCode(null);
             using(countryCodeConverter).map(source).setCountryCode(null);
         }
    }


    /**
     * Converts {@link  AddressDto } to {@link StateCode}}
     */
    @Component
    public static class StateCodeConverter extends AbstractConverter<AddressDto, StateCode> {

        private final StateCodeRepository stateCodeRepository;
        @Autowired
        public StateCodeConverter(StateCodeRepository stateCodeRepository) {
            this.stateCodeRepository = stateCodeRepository;
        }
        @Override
        protected StateCode convert(AddressDto source) {
            return stateCodeRepository.findByCode(source.getStateCode());
        }
    }


    /**
     * Converts {@link  AddressDto } to {@link CountryCode}}
     */
    @Component
     static class CountryCodeConverter extends AbstractConverter<AddressDto, CountryCode> {

        private final CountryCodeRepository countryCodeRepository;
        @Autowired
        public CountryCodeConverter(CountryCodeRepository CountryCodeRepository) {
            this.countryCodeRepository = CountryCodeRepository;
        }
        @Override
        protected CountryCode convert(AddressDto source) {
            return countryCodeRepository.findByCode(source.getCountryCode());
        }
    }
}
