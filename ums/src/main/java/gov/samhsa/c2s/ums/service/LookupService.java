package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.dto.LookupDto;
import gov.samhsa.c2s.ums.service.dto.RelationshipDto;
import gov.samhsa.c2s.ums.service.dto.RoleDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LookupService {

    @Transactional(readOnly = true)
    List<LookupDto> getLocales();


    @Transactional(readOnly = true)
    List<LookupDto> getStateCodes();


    @Transactional(readOnly = true)
    List<LookupDto> getCountryCodes();

    @Transactional(readOnly = true)
    List<LookupDto> getAdministrativeGenderCodes();

    @Transactional(readOnly = true)
    List<RoleDto> getRoles();

    @Transactional(readOnly = true)
    List<RelationshipDto> getRelationships();
}
