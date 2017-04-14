package gov.samhsa.c2s.ums.config;

import gov.samhsa.c2s.ums.domain.Role;
import gov.samhsa.c2s.ums.domain.RoleRepository;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCodeRepository;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.modelmapper.AbstractConverter;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class UserDtoToUserMap extends PropertyMap<UserDto, User> {

    private final AdministrativeGenderConverter genderConverter;
    private final RoleConverter roleConverter;

    @Autowired
    public UserDtoToUserMap(AdministrativeGenderConverter genderConverter, RoleConverter roleConverter) {
        this.genderConverter = genderConverter;
        this.roleConverter = roleConverter;
    }


    @Override
    protected void configure() {

        //Required Fields
        map().setFirstName(source.getFirstName());
        map().setLastName(source.getLastName());
        map().setBirthDay(source.getBirthDate());
        using(genderConverter).map(source).setAdministrativeGenderCode(null);
        skip().setAddress(null);
        using(roleConverter).map(source).setRoles(null);

    }


    /**
     * Converts {@link  UserDto } to {@link AdministrativeGenderCode}}
     */
    @Component
    public static class AdministrativeGenderConverter extends AbstractConverter<UserDto, AdministrativeGenderCode> {

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
     * Converts {@link  UserDto } to {@link AdministrativeGenderCode}}
     */
    @Component
    public static class RoleConverter extends AbstractConverter<UserDto, Set<Role>> {

        private final RoleRepository roleRepository;

        @Autowired
        public RoleConverter(RoleRepository roleRepository) {
            this.roleRepository = roleRepository;
        }

        @Override
        protected Set<Role> convert(UserDto source) {
             return roleRepository.findAllByCode(source.getRole());
        }
    }
}


