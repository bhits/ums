package gov.samhsa.c2s.ums.config;

import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCodeRepository;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.modelmapper.AbstractConverter;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserDtoToUserMap extends PropertyMap<UserDto, User> {

    private final AdministrativeGenderConverter genderConverter;


    @Autowired
    public UserDtoToUserMap(AdministrativeGenderConverter genderConverter) {
        this.genderConverter = genderConverter;
    }


    @Override
    protected void configure() {

        //Required Fields
        map().setFirstName(source.getFirstName());
        map().setLastName(source.getLastName());
        map().setBirthDay(source.getBirthDate());
        using(genderConverter).map(source).setAdministrativeGenderCode(null);
        map().setEmail(source.getEmail());

    }

    /**
     * Converts {@link AdministrativeGenderCode to {@link Set } of {@link UserDto}}
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
}
