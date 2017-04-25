package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.dto.PatientDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PatientService {

    @Transactional(readOnly = true)
    PatientDto getPatientByPatientId(String patientId, Optional<String> userAuthId);
}
