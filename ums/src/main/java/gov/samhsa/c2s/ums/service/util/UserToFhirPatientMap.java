package gov.samhsa.c2s.ums.service.util;


import gov.samhsa.c2s.ums.domain.Telecom;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCodeRepository;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Patient;
import org.modelmapper.AbstractConverter;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

public class UserToFhirPatientMap extends PropertyMap<User, Patient> {

    AdministrativeGenderConverter genderConverter;

    @Override
    protected void configure() {
        map().addName().setFamily(source.getLastName()).addGiven(source.getFirstName());
        for (Telecom telecom : source.getTelecoms()) {
            if (telecom.getSystem().equalsIgnoreCase(ContactPoint.ContactPointSystem.EMAIL.toCode())) {
                map().addTelecom().setValue(telecom.getValue()).setSystem(ContactPoint.ContactPointSystem.EMAIL);
            } else if (telecom.getSystem().equalsIgnoreCase(ContactPoint.ContactPointSystem.PHONE.toCode())) {
                map().addTelecom().setValue(telecom.getValue()).setSystem(ContactPoint.ContactPointSystem.PHONE);
            }
        }
        map().setBirthDate(
                Date.from(source.getBirthDay().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        using(genderConverter).map(source).setGender(null);

    }





    /**
     * Converts {@link  UserDto } to {@link AdministrativeGenderCode}}
     */
    @Component
    private static class AdministrativeGenderConverter extends AbstractConverter<UserDto, Enumerations.AdministrativeGender> {

        @Override
        protected Enumerations.AdministrativeGender convert(UserDto source) {
            String codeString = source.getGenderCode();
            if (codeString != null && !"".equals(codeString) || codeString != null && !"".equals(codeString)) {
                if ("male".equalsIgnoreCase(codeString) || "M".equalsIgnoreCase(codeString)) {
                    return Enumerations.AdministrativeGender.MALE;
                } else if ("female".equalsIgnoreCase(codeString) || "F".equalsIgnoreCase(codeString)) {
                    return Enumerations.AdministrativeGender.FEMALE;
                } else if ("other".equalsIgnoreCase(codeString) || "O".equalsIgnoreCase(codeString)) {
                    return Enumerations.AdministrativeGender.OTHER;
                } else if ("unknown".equalsIgnoreCase(codeString) || "UN".equalsIgnoreCase(codeString)) {
                    return Enumerations.AdministrativeGender.UNKNOWN;
                } else {
                    throw new IllegalArgumentException("Unknown AdministrativeGender code \'" + codeString + "\'");
                }
            } else {
                return null;
            }
        }
    }

}
