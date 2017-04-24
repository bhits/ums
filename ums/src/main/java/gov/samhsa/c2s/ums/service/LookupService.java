package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.dto.LocaleDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LookupService {

    @Transactional(readOnly = true)
    List<LocaleDto> getLocales();
}
