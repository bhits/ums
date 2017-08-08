package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.I18nMessage;
import gov.samhsa.c2s.ums.domain.I18nMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class I18nServiceImpl implements I18nService {
    private final String ROLE = "ROLE";
    private final String ADMINISTRATIVE_GENDER_CODE = "ADMINISTRATIVE_GENDER_CODE";

    @Autowired
    I18nMessageRepository i18nMessageRepository;

    @Override
    public Optional<I18nMessage> getI18nRoleName(String id) {
        String PROPERTY_NAME = "NAME";
        String locale = LocaleContextHolder.getLocale().getLanguage();
        String key = ROLE.concat(".").concat(id).concat(".").concat(PROPERTY_NAME);
        return i18nMessageRepository.findByKeyAndLocale( key, locale );
    }

    @Override
    public Optional<I18nMessage> getI18nGenderDisplayName(String id) {
        String PROPERTY_NAME = "DISPLAY_NAME";
        String locale = LocaleContextHolder.getLocale().getLanguage();
        String key = ADMINISTRATIVE_GENDER_CODE.concat(".").concat(id).concat(".").concat(PROPERTY_NAME);
        return i18nMessageRepository.findByKeyAndLocale( key, locale );
    }

}