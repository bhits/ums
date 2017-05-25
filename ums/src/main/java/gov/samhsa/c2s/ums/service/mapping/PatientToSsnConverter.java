package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.Identifier;
import gov.samhsa.c2s.ums.domain.Patient;
import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PatientToSsnConverter extends AbstractConverter<Patient, String> {
    @Autowired
    private UmsProperties umsProperties;

    @Override
    protected String convert(Patient patient) {
        return convertAsOptional(patient).orElse(null);
    }

    public Optional<String> convertAsOptional(Patient patient) {
        return patient.getDemographics().getIdentifiers().stream()
                .filter(id -> umsProperties.getSsn().getCodeSystem().equals(id.getSystem()))
                .map(Identifier::getValue)
                .findAny();
    }
}
