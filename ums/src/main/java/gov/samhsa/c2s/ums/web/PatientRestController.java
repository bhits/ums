package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.PatientService;
import gov.samhsa.c2s.ums.service.dto.PatientDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patients")
public class PatientRestController {

    @Autowired
    private PatientService patientService;

    @GetMapping(value = "/OAuth2/{oAuth2UserId}")
    PatientDto getPatientByOauth2UserId(@PathVariable String oAuth2UserId){
        return patientService.getPatientByOauth2UserId(oAuth2UserId);
    }


}
