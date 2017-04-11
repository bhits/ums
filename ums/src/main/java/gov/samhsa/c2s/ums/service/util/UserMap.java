package gov.samhsa.c2s.ums.service.util;

import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.reference.AbstractLocalDBLookupCodedConcept;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMap extends PropertyMap<UserDto, User> {

    private final UmsProperties umsProperties;

    @Autowired
    public UserMap(UmsProperties umsProperties) {
        this.umsProperties = umsProperties;
    }

    @Override
    protected void configure() {
        //Required Fields
        map().setFirstName(source.getFirstName());
        map().setLastName(source.getLastName());
        map().setBirthDay(source.getBirthDate());

        AdministrativeGenderCode genderCode = new AdministrativeGenderCode();
        setSystemValues(genderCode, umsProperties.getGender().getDisplayName(), source.getGenderCode());
        map().setAdministrativeGenderCode(genderCode);

        map().setEmail(source.getEmail());

    }

    private void setSystemValues(AbstractLocalDBLookupCodedConcept codedConcept, String type, String code){
        switch (type){
            case "SSN":

                break;
            case "GENDER":
                codedConcept.setCode(code);
                codedConcept.setCodeSystem(umsProperties.getGender().getCodeSystem());
                codedConcept.setCodeSystemOID(umsProperties.getGender().getCodeSystemOID());
                codedConcept.setDisplayName(umsProperties.getGender().getDisplayName());
                break;
            default:
                throw new IllegalArgumentException("Invalid identifier type");
        }
    }
}
