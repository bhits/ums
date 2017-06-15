package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.IdentifierSystem;
import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
public class IdentifierSystemToRequiredIdentifierSystemsConverter extends AbstractConverter<IdentifierSystem, Map<String, List<UmsProperties.RequiredIdentifierSystem>>> {
    @Autowired
    private UmsProperties umsProperties;

    @Override
    protected Map<String, List<UmsProperties.RequiredIdentifierSystem>> convert(IdentifierSystem identifierSystem) {
        return umsProperties.getRequiredIdentifierSystemsByRole().entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(requiredIdentifierSystem -> identifierSystem.getSystem().equals(requiredIdentifierSystem.getSystem())))
                .collect(toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue().stream()
                                .filter(requiredIdentifierSystem -> requiredIdentifierSystem.getSystem().equals(identifierSystem.getSystem()))
                                .collect(toList())));
    }
}