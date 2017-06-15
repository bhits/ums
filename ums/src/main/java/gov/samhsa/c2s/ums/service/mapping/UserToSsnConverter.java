package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.Demographics;
import gov.samhsa.c2s.ums.domain.Identifier;
import gov.samhsa.c2s.ums.domain.User;
import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class UserToSsnConverter extends AbstractConverter<User, Optional<String>> {
    @Autowired
    private UmsProperties umsProperties;

    @Override
    protected Optional<String> convert(User user) {
        return convertAsOptional(user);
    }

    public Optional<String> convertAsOptional(User user) {
        return Optional.of(user)
                .map(User::getDemographics)
                .map(Demographics::getIdentifiers)
                .orElseGet(Collections::emptyList).stream()
                .filter(id -> umsProperties.getSsn().getCodeSystem().equals(id.getIdentifierSystem().getSystem()))
                .map(Identifier::getValue)
                .findAny();
    }
}
