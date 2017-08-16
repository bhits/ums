package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.common.i18n.service.I18nService;
import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.IdentifierSystemRepository;
import gov.samhsa.c2s.ums.domain.Locale;
import gov.samhsa.c2s.ums.domain.LocaleRepository;
import gov.samhsa.c2s.ums.domain.Role;
import gov.samhsa.c2s.ums.domain.RoleRepository;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCodeRepository;
import gov.samhsa.c2s.ums.domain.reference.CountryCode;
import gov.samhsa.c2s.ums.domain.reference.CountryCodeRepository;
import gov.samhsa.c2s.ums.domain.reference.StateCode;
import gov.samhsa.c2s.ums.domain.reference.StateCodeRepository;
import gov.samhsa.c2s.ums.service.dto.IdentifierSystemDto;
import gov.samhsa.c2s.ums.service.dto.LookupDto;
import gov.samhsa.c2s.ums.service.dto.RoleDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class LookupServiceImpl implements LookupService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private LocaleRepository localeRepository;

    @Autowired
    private StateCodeRepository stateCodeRepository;

    @Autowired
    private CountryCodeRepository countryCodeRepository;

    @Autowired
    private AdministrativeGenderCodeRepository administrativeGenderCodeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private IdentifierSystemRepository identifierSystemRepository;

    @Autowired
    private I18nService i18nService;

    @Override
    @Transactional(readOnly = true)
    public List<LookupDto> getLocales() {
        final List<Locale> locales = localeRepository.findAll();
        return locales.stream()
                .map(locale -> modelMapper.map(locale, LookupDto.class))
                .collect(toList());
    }

    @Override
    public List<LookupDto> getStateCodes() {
        final List<StateCode> stateCodes = stateCodeRepository.findAll();
        return stateCodes.stream()
                .map(stateCode -> modelMapper.map(stateCode, LookupDto.class))
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LookupDto> getCountryCodes() {
        final List<CountryCode> countryCodes = countryCodeRepository.findAll();
        return countryCodes.stream()
                .map(countryCode -> modelMapper.map(countryCode, LookupDto.class))
                .collect(toList());
    }

    @Override
    public List<LookupDto> getAdministrativeGenderCodes() {
        final List<AdministrativeGenderCode> genderCodes = administrativeGenderCodeRepository.findAll();

        return genderCodes.stream()
                .map(administrativeGenderCode -> {
                    final LookupDto lookupDto = modelMapper.map(administrativeGenderCode, LookupDto.class);
                    lookupDto.setDisplayName(i18nService.getI18nMessage(administrativeGenderCode, "displayName",
                            administrativeGenderCode::getDisplayName));
                    return lookupDto;
                })
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getRoles() {
        final List<Role> roles = roleRepository.findAll();

        return roles.stream()
                .map(role -> {
                    final RoleDto roleDto = modelMapper.map(role, RoleDto.class);
                    roleDto.setName(i18nService.getI18nMessage(role, "name", role::getName));
                    return roleDto;
                })
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdentifierSystemDto> getIdentifierSystems(Optional<Boolean> systemGenerated) {
        return identifierSystemRepository.findAll().stream()
                .map(identifierSystem -> modelMapper.map(identifierSystem, IdentifierSystemDto.class))
                .filter(identifierSystemDto -> {
                    if (systemGenerated.isPresent()) {
                        final boolean sg = systemGenerated.get();
                        if (sg == false &&
                                (Objects.isNull(identifierSystemDto.getRequiredIdentifierSystemsByRole()) ||
                                        identifierSystemDto.getRequiredIdentifierSystemsByRole().isEmpty())) {
                            return true;
                        }
                        return identifierSystemDto.getRequiredIdentifierSystemsByRole().entrySet().stream()
                                .map(Map.Entry::getValue)
                                .flatMap(List::stream)
                                .map(UmsProperties.RequiredIdentifierSystem::getAlgorithm)
                                .filter(algorithm -> sg ? !algorithm.equals(UmsProperties.Algorithm.NONE) : algorithm.equals(UmsProperties.Algorithm.NONE))
                                .findAny().isPresent();
                    } else {
                        return true;
                    }
                }).collect(toList());
    }
}