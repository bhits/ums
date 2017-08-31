package gov.samhsa.c2s.ums.service.fhir;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.service.dto.FhirPatientDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.FHIRFormatErrorException;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.function.Function;

@Service
@Slf4j
public class FhirPatientServiceImpl implements FhirPatientService {

    @Autowired
    private UmsProperties umsProperties;

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
    @Autowired
    private FhirContext fhirContext;
    @Autowired
    private IGenericClient fhirClient;
    @Autowired
    private FhirValidator fhirValidator;

    @Override
    public void publishFhirPatient(UserDto userDto) {
        final Patient patient = createFhirPatient(userDto);
        if (log.isDebugEnabled()) {
            log.debug("FHIR Patient:");
            log.debug(fhirContext.newXmlParser().setPrettyPrint(true)
                    .encodeResourceToString(patient));
            log.debug(fhirContext.newJsonParser().setPrettyPrint(true)
                    .encodeResourceToString(patient));
        }

        final ValidationResult validationResult = fhirValidator.validateWithResult(patient);
        if (validationResult.isSuccessful()) {
            RestTemplate restTemplate =  new RestTemplate();
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            headers.add("Content-Type", "application/json");
            FhirPatientDto fhirPatientDto = new FhirPatientDto(patient);
//            HttpEntity<FhirPatientDto> request = new HttpEntity<FhirPatientDto>(fhirPatientDto, headers);
            HttpEntity<Patient> request = new HttpEntity<Patient>(patient, headers);
            String url  = umsProperties.getFhir().getPublish().getServerUrl();
            try {
                Object result = restTemplate.postForObject(url, patient, Object.class);
                log.error("Result" ,result);
            }catch(Exception e){
                log.error("Error Message" ,e);
            }
//             fhirClient.create().resource(patient).execute();
        } else {
            throw new FHIRFormatErrorException("FHIR Patient Validation is not successful" + validationResult.getMessages());
        }

    }

    @Override
    public void updateFhirPatient(UserDto userDto) {
        final Patient patient = createFhirPatient(userDto);
        final ValidationResult validationResult = fhirValidator.validateWithResult(patient);
        if (validationResult.isSuccessful()) {
            if (umsProperties.getFhir().getPublish().isUseCreateForUpdate()) {
                log.debug("Calling FHIR Patient Create for Update based on the configuration");
                fhirClient.create().resource(patient).execute();
            } else {
                log.debug("Calling FHIR Patient Update for Update based on the configuration");
                fhirClient.update().resource(patient)
                        .conditional()
                        .where(Patient.IDENTIFIER.exactly().systemAndCode(umsProperties.getMrn().getCodeSystem(), patient.getId()))
                        .execute();
            }
        } else {
            throw new FHIRFormatErrorException("FHIR Patient Validation is not successful" + validationResult.getMessages());
        }
    }

    @Override
    public Patient createFhirPatient(UserDto userDto) {
        return userDtoToPatient.apply(userDto);
    }

    private void setIdentifiers(Patient patient, UserDto userDto) {

        //setting patient mrn
        patient.addIdentifier().setSystem(umsProperties.getMrn().getCodeSystem())
                .setUse(Identifier.IdentifierUse.OFFICIAL).setValue(userDto.getMrn());

        patient.setId(new IdType(userDto.getMrn()));

        // setting ssn value
        userDto.getSocialSecurityNumber()
                .map(String::trim)
                .ifPresent(ssnValue -> patient.addIdentifier().setSystem(umsProperties.getSsn().getCodeSystem())
                        .setValue(ssnValue));
    }
}


