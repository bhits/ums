package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.Locale;
import gov.samhsa.c2s.ums.domain.LocaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocaleServiceImpl implements LocaleService {
    private final LocaleRepository localeRepository;

    @Autowired
    public LocaleServiceImpl(LocaleRepository localeRepository) {
        this.localeRepository = localeRepository;
    }

    @Override
    public Locale findLocaleByCode(String code) {
        return localeRepository.findByCode(code);
    }

}
