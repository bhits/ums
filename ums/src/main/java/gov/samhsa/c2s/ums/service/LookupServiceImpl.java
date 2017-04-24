package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.Locale;
import gov.samhsa.c2s.ums.domain.LocaleRepository;
import gov.samhsa.c2s.ums.service.dto.LocaleDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class LookupServiceImpl implements LookupService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    LocaleRepository localeRepository;


    @Override
    public List<LocaleDto> getLocales() {
        final List<Locale> locales = localeRepository.findAll();
        return locales.stream()
                .map(locale -> modelMapper.map(locale, LocaleDto.class))
                .collect(toList());
    }
}