package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.dto.IdentifierSystemDto;
import gov.samhsa.c2s.ums.service.dto.PatientDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PatientService {

    @Transactional(readOnly = true)
    PatientDto getPatientByPatientId(String patientId, Optional<String> userAuthId);

    @Transactional(readOnly = true)
    PatientDto getPatientByIdentifierValueAndIdentifierSystem(String identifierValue, String identifierSystem);

    @Transactional(readOnly = true)
    List<PatientDto> getPatientByUserAuthId(String userAuthId);

    @Transactional(readOnly = true)
    IdentifierSystemDto getPatientMrnIdentifierSystemByPatientId(String patientId);
}
