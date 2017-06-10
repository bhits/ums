package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.domain.IdentifierSystem;
import gov.samhsa.c2s.ums.service.dto.IdentifierSystemDto;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdentifierSystemToIdentifierSystemDtoMap extends PropertyMap<IdentifierSystem, IdentifierSystemDto> {

    @Autowired
    private IdentifierSystemToRequiredIdentifierSystemsConverter identifierSystemToRequiredIdentifierSystemsConverter;

    @Override
    protected void configure() {
        using(identifierSystemToRequiredIdentifierSystemsConverter).map(source).setRequiredIdentifierSystemsByRole(null);
    }
}
