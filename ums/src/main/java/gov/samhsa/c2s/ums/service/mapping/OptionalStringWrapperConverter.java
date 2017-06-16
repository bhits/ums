package gov.samhsa.c2s.ums.service.mapping;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OptionalStringWrapperConverter extends AbstractConverter<String, Optional<String>> {

    @Override
    protected Optional<String> convert(String s) {
        return Optional.ofNullable(s);
    }
}
