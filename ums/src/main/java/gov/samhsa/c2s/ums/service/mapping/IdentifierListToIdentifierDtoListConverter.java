package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.domain.Identifier;
import gov.samhsa.c2s.ums.service.dto.IdentifierDto;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Component
public class IdentifierListToIdentifierDtoListConverter extends AbstractConverter<List<Identifier>, Optional<List<IdentifierDto>>> {

    @Override
    protected Optional<List<IdentifierDto>> convert(List<Identifier> identifiers) {
        final List<IdentifierDto> identifierDtos = identifiers.stream()
                .map(identifier -> IdentifierDto.of(identifier.getValue(), identifier.getIdentifierSystem().getSystem()))
                .collect(toList());
        return Optional.of(identifierDtos).filter(list -> !list.isEmpty());
    }
}
