package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.domain.Locale;
import gov.samhsa.c2s.ums.domain.Role;
import gov.samhsa.c2s.ums.domain.RoleRepository;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCodeRepository;
import gov.samhsa.c2s.ums.service.LocaleService;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.modelmapper.AbstractConverter;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserDtoToUserMap extends PropertyMap<UserDto, User> {

    private final AdministrativeGenderConverter genderConverter;
    private final RoleConverter roleConverter;
    private final LocaleConverter localeConverter;

    public UserDtoToUserMap(AdministrativeGenderConverter genderConverter, RoleConverter roleConverter, LocaleConverter localeConverter) {
        this.genderConverter = genderConverter;
        this.roleConverter = roleConverter;
        this.localeConverter = localeConverter;
    }


    @Override
    protected void configure() {
        //Required Fields
        map().getDemographics().setFirstName(source.getFirstName());
        map().getDemographics().setMiddleName(source.getMiddleName());
        map().getDemographics().setLastName(source.getLastName());
        map().getDemographics().setBirthDay(source.getBirthDate());
        using(genderConverter).map(source).getDemographics().setAdministrativeGenderCode(null);
        skip().getDemographics().setAddresses(null);
        using(roleConverter).map(source).setRoles(null);
        using(localeConverter).map(source).setLocale(null);
    }

    /**
     * Converts {@link  UserDto } to {@link AdministrativeGenderCode}}
     */
    @Component
    private static class AdministrativeGenderConverter extends AbstractConverter<UserDto, AdministrativeGenderCode> {

        private final AdministrativeGenderCodeRepository genderCodeRepository;

        @Autowired
        public AdministrativeGenderConverter(AdministrativeGenderCodeRepository genderCodeRepository) {
            this.genderCodeRepository = genderCodeRepository;
        }

        @Override
        protected AdministrativeGenderCode convert(UserDto source) {
            return genderCodeRepository.findByCode(source.getGenderCode());
        }
    }


    /**
     * Converts {@link  UserDto } to {@link Role}}
     */
    @Component
    private static class RoleConverter extends AbstractConverter<UserDto, Set<Role>> {
        private final RoleRepository roleRepository;

        @Autowired
        public RoleConverter(RoleRepository roleRepository) {
            this.roleRepository = roleRepository;
        }

        @Override
        protected Set<Role> convert(UserDto source) {
            return source.getRoles().stream().flatMap(roleDto -> roleRepository.findAllByCode(roleDto.getCode()).stream()).collect(Collectors.toSet());
        }
    }


    /**
     * Converts {@link  UserDto } to {@link Locale}}
     */
    @Component
    private static class LocaleConverter extends AbstractConverter<UserDto, Locale> {
        private final LocaleService localeService;

        @Autowired
        public LocaleConverter(LocaleService localeService) {
            this.localeService = localeService;
        }

        @Override
        protected Locale convert(UserDto source) {
            return localeService.findLocaleByCode(source.getLocale());
        }
    }

}
