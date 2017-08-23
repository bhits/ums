package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.PatientService;
import gov.samhsa.c2s.ums.service.dto.IdentifierSystemDto;
import gov.samhsa.c2s.ums.service.dto.PatientDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/patients")
public class PatientRestController {

    @Autowired
    private PatientService patientService;

    @GetMapping(value = "/{patientId}")
    PatientDto getPatientByPatientId(@PathVariable String patientId,
                                     @RequestParam Optional<String> userAuthId) {
        return patientService.getPatientByPatientId(patientId, userAuthId);
    }

    @GetMapping(value = "/authId/{userAuthId}")
    List<PatientDto> getPatientByUserAuthId(@PathVariable String userAuthId) {
        return patientService.getPatientByUserAuthId(userAuthId);
    }

    @GetMapping(value = "/{patientId}/mrn-identifier-system")
    @ResponseStatus(HttpStatus.OK)
    public IdentifierSystemDto getPatientMrnIdentifierSystemByPatientId(@PathVariable String patientId) {
        return patientService.getPatientMrnIdentifierSystemByPatientId(patientId);
    }


}
