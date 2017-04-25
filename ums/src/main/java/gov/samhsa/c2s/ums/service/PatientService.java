package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.dto.PatientDto;
import org.springframework.transaction.annotation.Transactional;

public interface PatientService {

    @Transactional(readOnly = true)
    PatientDto getPatientByOauth2UserId(String oAuth2UserId);
}
