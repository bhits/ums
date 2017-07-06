package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.Locale;
import org.springframework.stereotype.Service;

@Service
public interface LocaleService {
    Locale findLocaleByCode(String code);
}
