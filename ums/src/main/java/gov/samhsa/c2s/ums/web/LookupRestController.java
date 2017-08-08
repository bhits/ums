package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.LookupService;
import gov.samhsa.c2s.ums.service.dto.IdentifierSystemDto;
import gov.samhsa.c2s.ums.service.dto.LookupDto;
import gov.samhsa.c2s.ums.service.dto.RoleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
public class LookupRestController {

    @Autowired
    private LookupService lookupService;

    @GetMapping("/locales")
    public List<LookupDto> getLocales() {
        return lookupService.getLocales();
    }

    @GetMapping("/statecodes")
    public List<LookupDto> getStateCodes() {
        return lookupService.getStateCodes();
    }

    @GetMapping("/countrycodes")
    public List<LookupDto> getCountryCodes() {
        return lookupService.getCountryCodes();
    }

    @GetMapping("/gendercodes")
    public List<LookupDto> getAdministrativeGenderCodes() {
        return lookupService.getAdministrativeGenderCodes();
    }

    @GetMapping("/roles")
    public List<RoleDto> getRoles() {
        return lookupService.getRoles();
    }

    @GetMapping("/identifierSystems")
    public List<IdentifierSystemDto> getIdentifierSystems(@RequestParam Optional<Boolean> systemGenerated) {
        return lookupService.getIdentifierSystems(systemGenerated);
    }
}
