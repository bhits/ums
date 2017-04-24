package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.LookupService;
import gov.samhsa.c2s.ums.service.dto.LocaleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LookupRestController {


    @Autowired
    private LookupService lookupService;

    @GetMapping("/locale")
    public List<LocaleDto> getLocales() {
        return lookupService.getLocales();
    }


}
