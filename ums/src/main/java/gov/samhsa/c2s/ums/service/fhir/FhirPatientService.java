package gov.samhsa.c2s.ums.service.fhir;


import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.hl7.fhir.dstu3.model.Patient;

public interface FhirPatientService {

  /* converts UserDto to fhir patient object */
  public Patient getFhirPatient(UserDto userDto);

}
