package gov.samhsa.c2s.ums.service.fhir;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.IClientExecutable;
import ca.uhn.fhir.rest.gclient.ICreateTyped;
import ca.uhn.fhir.rest.gclient.IUpdateTyped;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.service.dto.UpdateUserLimitedFieldsDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.FHIRFormatErrorException;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Service
@Slf4j
public class FhirPatientServiceImpl implements FhirPatientService {


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
    @Autowired
    private UmsProperties umsProperties;
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
                    fhirPatient.addAddress().addLine(addressDto.getLine1()).addLine(addressDto.getLine2()).setCity(addressDto.getCity()).setState(addressDto.getStateCode()).setPostalCode(addressDto.getPostalCode()).setCountry(addressDto.getCountryCode())
            );

            userDto.getTelecoms().stream().forEach(telecomDto ->
                    fhirPatient.addTelecom().setSystem(ContactPoint.ContactPointSystem.valueOf(telecomDto.getSystem())).setUse(ContactPoint.ContactPointUse.valueOf(telecomDto.getUse())).setValue(telecomDto.getValue())
            );

            return fhirPatient;
        }
    };

    Function<User, Patient> updateUserLimitedFieldsToPatient = new Function<User, Patient>() {
        @Override
        public Patient apply(User user) {
            //Set patient information
            final String mrnIdentifierSystem = umsProperties.getMrn().getCodeSystem();
            final String ssnIdentifierSystem = umsProperties.getSsn().getCodeSystem();
            Patient fhirPatient = new Patient();
            fhirPatient.addName().setFamily(user.getDemographics().getLastName()).addGiven(user.getDemographics().getFirstName());
            fhirPatient.setBirthDate(Date.valueOf(user.getDemographics().getBirthDay()));
            fhirPatient.setGender(getPatientGender.apply(user.getDemographics().getAdministrativeGenderCode().toString()));
            fhirPatient.setActive(true);

            //Add identifiers
            setIdentifiersForLimitedFields(fhirPatient, user, mrnIdentifierSystem);
            setIdentifiersForLimitedFields(fhirPatient, user, ssnIdentifierSystem);

            //Optional fields
            user.getDemographics().getAddresses().stream().forEach(address ->
                    fhirPatient.addAddress().addLine(address.getLine2()).addLine(address.getLine1()).setCity(address.getCity()).setState(address.getStateCode().getDisplayName()).setPostalCode(address.getPostalCode()).setCountry(address.getCountryCode().getDisplayName()));

            user.getDemographics().getTelecoms().stream().forEach(telecom ->
                    fhirPatient.addTelecom()
                            .setSystem(ContactPoint.ContactPointSystem.valueOf(telecom.getSystem().toString()))
                            .setUse(ContactPoint.ContactPointUse.valueOf(telecom.getUse().toString()))
                            .setValue(telecom.getValue())
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
            applyRequestEncoding(fhirClient.create().resource(patient)).execute();
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
                applyRequestEncoding(fhirClient.create().resource(patient)).execute();
            } else {
                log.debug("Calling FHIR Patient Update for Update based on the configuration");
                applyRequestEncoding(fhirClient.update().resource(patient))
                        .conditional()
                        .where(Patient.IDENTIFIER.exactly().systemAndCode(umsProperties.getMrn().getCodeSystem(), patient.getId()))
                        .execute();
            }
        } else {
            throw new FHIRFormatErrorException("FHIR Patient Validation is not successful" + validationResult.getMessages());
        }
    }

    @Override
    public void updateFhirPatientWithLimitedField(User user) {
        final Patient patient = createFhirPatientWithLimitedField(user);
        final ValidationResult validationResult = fhirValidator.validateWithResult(patient);
        if (validationResult.isSuccessful()) {
            if (umsProperties.getFhir().getPublish().isUseCreateForUpdate()) {
                log.debug("Calling FHIR Patient Create for Update based on the configuration");
                applyRequestEncoding(fhirClient.create().resource(patient)).execute();
            } else {
                log.debug("Calling FHIR Patient Update for Update based on the configuration");
                applyRequestEncoding(fhirClient.update().resource(patient))
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

    @Override
    public Patient createFhirPatientWithLimitedField(User user) {
        return updateUserLimitedFieldsToPatient.apply(user);
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

    private void setIdentifiersForLimitedFields(Patient patient, User user, String idSystem) {
        user.getDemographics().getIdentifiers().stream()
                .filter(identifier -> {
                    final String system = identifier.getIdentifierSystem().getSystem();
                    return idSystem.equals(system);
                })
                .forEach(identifier -> {
                    final String identifierSystem = identifier.getIdentifierSystem().getSystem();
                    final String identifierValue = identifier.getValue();
                    final Identifier fhirIdentifier = patient.addIdentifier();
                    fhirIdentifier.setSystem(identifierSystem);
                    fhirIdentifier.setValue(identifierValue);
                    if (identifierSystem.equals(umsProperties.getMrn().getCodeSystem())) {
                        fhirIdentifier.setUse(Identifier.IdentifierUse.OFFICIAL);
                        patient.setId(new IdType(identifierValue));
                    }
                });
    }


    private ICreateTyped applyRequestEncoding(ICreateTyped request) {
        return (ICreateTyped) applyRequestEncodingFromConfig(request);
    }

    private IUpdateTyped applyRequestEncoding(IUpdateTyped request) {
        return (IUpdateTyped) applyRequestEncodingFromConfig(request);
    }

    private IClientExecutable applyRequestEncodingFromConfig(IClientExecutable request) {
        switch (umsProperties.getFhir().getPublish().getEncoding()) {
            case XML:
                request.encodedXml();
                break;
            case JSON:
            default:
                request.encodedJson();
                break;
        }
        return request;
    }
}


