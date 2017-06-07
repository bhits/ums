package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.domain.Identifier;
import gov.samhsa.c2s.ums.service.dto.IdentifierDto;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class IdentifierListToIdentifierDtoListConverter extends AbstractConverter<List<Identifier>, List<IdentifierDto>> {

    @Override
    protected List<IdentifierDto> convert(List<Identifier> identifiers) {
        return identifiers.stream()
                .map(identifier -> IdentifierDto.of(identifier.getValue(), identifier.getIdentifierSystem().getSystem()))
                .collect(toList());
    }
}
