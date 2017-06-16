package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.Demographics;
import gov.samhsa.c2s.ums.domain.Identifier;
import gov.samhsa.c2s.ums.domain.Patient;
import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class PatientToMrnConverter extends AbstractConverter<Patient, String> {
    @Autowired
    private UmsProperties umsProperties;

    @Override
    public String convert(Patient patient) {
        return convertAsOptional(patient).orElse(null);
    }

    public Optional<String> convertAsOptional(Patient patient) {
        return Optional.of(patient)
                .map(Patient::getDemographics)
                .map(Demographics::getIdentifiers)
                .orElseGet(Collections::emptyList).stream()
                .filter(id -> umsProperties.getMrn().getCodeSystem().equals(id.getIdentifierSystem().getSystem()))
                .map(Identifier::getValue)
                .findAny();
    }
}
