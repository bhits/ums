package gov.samhsa.c2s.ums.service.fhir;


import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FhirPatientServiceImpl implements FhirPatientService {

    private final UmsProperties umsProperties;

    @Autowired
    public FhirPatientServiceImpl(UmsProperties umsProperties) {
        this.umsProperties = umsProperties;
    }


    @Override
    public Patient getFhirPatient(UserDto userDto) {
        return new Patient();
    }


   /* Function<UserDto, Patient> patientDtoToPatient = new Function<UserDto, Patient>() {
        @Override
        public Patient apply(UserDto userDto) {
            // set patient information
            Patient fhirPatient = new Patient();

            //setting mandatory fields

            fhirPatient.addName().setFamily(userDto.getLastName()).addGiven(userDto.getFirstName());
            fhirPatient.addTelecom().setValue(userDto.getEmail()).setSystem(ContactPoint.ContactPointSystem.EMAIL);
            fhirPatient.setBirthDate(userDto.getBirthDate());
            fhirPatient.setGender(getPatientGender.apply(userDto.getGenderCode()));
            fhirPatient.setActive(true);

            //Add an Identifier
            setIdentifiers(fhirPatient, userDto);

            //optional fields
            fhirPatient.addAddress().addLine(userDto.getAddress()).setCity(userDto.getCity()).setState(userDto.getStateCode()).setPostalCode(userDto.getZip());
            return fhirPatient;
        }
    };*/



}
