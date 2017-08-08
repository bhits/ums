package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.I18nMessage;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface I18nService {
    @Transactional
    Optional<I18nMessage> getI18nRoleName(String id);

    @Transactional
    Optional<I18nMessage> getI18nGenderDisplayName(String id);

}
