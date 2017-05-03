package gov.samhsa.c2s.ums.service.fhir;


import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.FHIRFormatErrorException;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.function.Function;

@Service
public class FhirPatientServiceImpl implements FhirPatientService {

    @Autowired
    private UmsProperties umsProperties;

    @Autowired
    private IGenericClient fhirClient;

    @Autowired
    private FhirValidator fhirValidator;


    @Override
    public void publishFhirPatient(UserDto userDto) {
        Patient patient = createFhirPatient(userDto);
        ValidationResult validationResult = fhirValidator.validateWithResult(patient);
        if (validationResult.isSuccessful())
            fhirClient.create().resource(patient).execute();
        else
            throw new FHIRFormatErrorException("FHIR Patient Validation is not successful" + validationResult.getMessages());
    }

    @Override
    public Patient createFhirPatient(UserDto userDto) {
        return userDtoToPatient.apply(userDto);
    }

    Function<UserDto, Patient> userDtoToPatient = new Function<UserDto, Patient>() {
        @Override
        public Patient apply(UserDto userDto) {
            // set patient information
            Patient fhirPatient = new Patient();

            //setting mandatory fields
            fhirPatient.addName().setFamily(userDto.getLastName()).addGiven(userDto.getFirstName());
            fhirPatient.setBirthDate(Date.valueOf(userDto.getBirthDate()));
            fhirPatient.setGender(getPatientGender.apply(userDto.getGenderCode()));
            fhirPatient.setActive(true);

            //Add an Identifier
            setIdentifiers(fhirPatient, userDto);

            //optional fields
            userDto.getAddresses().stream().forEach(addressDto ->
                    fhirPatient.addAddress().addLine(addressDto.getLine1()).addLine(addressDto.getLine2()).setCity(addressDto.getCity()).setState(addressDto.getStateCode()).setPostalCode(addressDto.getPostalCode())
            );

            userDto.getTelecoms().stream().forEach(telecomDto ->
                    fhirPatient.addTelecom().setSystem(ContactPoint.ContactPointSystem.valueOf(telecomDto.getSystem())).setUse(ContactPoint.ContactPointUse.valueOf(telecomDto.getUse())).setValue(telecomDto.getValue())
            );

            return fhirPatient;
        }
    };


    private void setIdentifiers(Patient patient, UserDto userDto) {

        //setting patient mrn
        patient.addIdentifier().setSystem(umsProperties.getMrn().getCodeSystem())
                .setUse(Identifier.IdentifierUse.OFFICIAL).setValue(userDto.getMrn());

        patient.setId(new IdType(userDto.getMrn()));

        // setting ssn value
        String ssnValue = userDto.getSocialSecurityNumber();
        if (null != ssnValue && !ssnValue.isEmpty())
            patient.addIdentifier().setSystem(umsProperties.getSsn().getCodeSystem())
                    .setValue(ssnValue);
    }

    Function<String, Enumerations.AdministrativeGender> getPatientGender = new Function<String, Enumerations.AdministrativeGender>() {
        @Override
        public Enumerations.AdministrativeGender apply(String codeString) {
            switch (codeString.toUpperCase()) {
                case "MALE":
                    return Enumerations.AdministrativeGender.MALE;
                case "M":
                    return Enumerations.AdministrativeGender.MALE;
                case "FEMALE":
                    return Enumerations.AdministrativeGender.FEMALE;
                case "F":
                    return Enumerations.AdministrativeGender.FEMALE;
                case "OTHER":
                    return Enumerations.AdministrativeGender.OTHER;
                case "O":
                    return Enumerations.AdministrativeGender.OTHER;
                case "UNKNOWN":
                    return Enumerations.AdministrativeGender.UNKNOWN;
                case "UN":
                    return Enumerations.AdministrativeGender.UNKNOWN;
                default:
                    return Enumerations.AdministrativeGender.UNKNOWN;

            }

        }
    };
}


