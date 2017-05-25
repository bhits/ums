package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.Identifier;
import gov.samhsa.c2s.ums.domain.User;
import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserToSsnConverter extends AbstractConverter<User, String> {
    @Autowired
    private UmsProperties umsProperties;

    @Override
    protected String convert(User user) {
        return convertAsOptional(user).orElse(null);
    }

    public Optional<String> convertAsOptional(User user) {
        return user.getDemographics().getIdentifiers().stream()
                .filter(id -> umsProperties.getSsn().getCodeSystem().equals(id.getSystem().getSystem()))
                .map(Identifier::getValue)
                .findAny();
    }
}
