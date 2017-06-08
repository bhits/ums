package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.Locale;
import gov.samhsa.c2s.ums.domain.LocaleRepository;
import gov.samhsa.c2s.ums.domain.RelationshipRepository;
import gov.samhsa.c2s.ums.domain.Role;
import gov.samhsa.c2s.ums.domain.RoleRepository;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCodeRepository;
import gov.samhsa.c2s.ums.domain.reference.CountryCode;
import gov.samhsa.c2s.ums.domain.reference.CountryCodeRepository;
import gov.samhsa.c2s.ums.domain.reference.StateCode;
import gov.samhsa.c2s.ums.domain.reference.StateCodeRepository;
import gov.samhsa.c2s.ums.service.dto.LookupDto;
import gov.samhsa.c2s.ums.service.dto.RoleDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LookupServiceImplTest {

    @Mock
    ModelMapper modelMapper;

    @Mock
    LocaleRepository localeRepository;

    @Mock
    StateCodeRepository stateCodeRepository;

    @Mock
    CountryCodeRepository countryCodeRepository;

    @Mock
    AdministrativeGenderCodeRepository administrativeGenderCodeRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    RelationshipRepository relationshipRepository;

    @InjectMocks
    LookupServiceImpl lookupServiceImpl;

    @Test
    public void testGetLocales(){
        //Arrange
        Locale locale1=mock(Locale.class);
        Locale locale2=mock(Locale.class);
        Locale locale3=mock(Locale.class);

        List<Locale> locales=new ArrayList<>();

        locales.add(locale1);
        locales.add(locale2);
        locales.add(locale3);

        LookupDto lookupDto1=mock(LookupDto.class);
        LookupDto lookupDto2=mock(LookupDto.class);
        LookupDto lookupDto3=mock(LookupDto.class);

        List<LookupDto> lookupDtos=new ArrayList<>();
        lookupDtos.add(lookupDto1);
        lookupDtos.add(lookupDto2);
        lookupDtos.add(lookupDto3);

        when(localeRepository.findAll()).thenReturn(locales);
        when(modelMapper.map(locale1,LookupDto.class)).thenReturn(lookupDto1);
        when(modelMapper.map(locale2,LookupDto.class)).thenReturn(lookupDto2);
        when(modelMapper.map(locale3,LookupDto.class)).thenReturn(lookupDto3);

        //Act
        List<LookupDto> getLocales=lookupServiceImpl.getLocales();

        //Assert
        assertEquals(lookupDtos,getLocales);
    }

    @Test
    public void testGetStateCodes(){
        StateCode stateCode1= mock(StateCode.class);
        StateCode stateCode2=mock(StateCode.class);

        List<StateCode> stateCodes=new ArrayList<>();
        stateCodes.add(stateCode1);
        stateCodes.add(stateCode2);

        LookupDto lookupDto1=mock(LookupDto.class);
        LookupDto lookupDto2=mock(LookupDto.class);

        List<LookupDto> lookupDtos=new ArrayList<>();
        lookupDtos.add(lookupDto1);
        lookupDtos.add(lookupDto2);

        when(stateCodeRepository.findAll()).thenReturn(stateCodes);

        when(modelMapper.map(stateCode1,LookupDto.class)).thenReturn(lookupDto1);
        when(modelMapper.map(stateCode2,LookupDto.class)).thenReturn(lookupDto2);

        //Act
        List<LookupDto> getStateCodes=lookupServiceImpl.getStateCodes();

        //Assert
        assertEquals(lookupDtos,getStateCodes);
    }

    @Test
    public void testGetCountryCodes(){
        //Arrange
        CountryCode countryCode1=mock(CountryCode.class);
        CountryCode countryCode2=mock(CountryCode.class);

        List<CountryCode> countryCodes=new ArrayList<>();
        countryCodes.add(countryCode1);
        countryCodes.add(countryCode2);


        LookupDto lookupDto1=mock(LookupDto.class);
        LookupDto lookupDto2=mock(LookupDto.class);

        List<LookupDto> lookupDtos=new ArrayList<>();
        lookupDtos.add(lookupDto1);
        lookupDtos.add(lookupDto2);

        when(countryCodeRepository.findAll()).thenReturn(countryCodes);

        when(modelMapper.map(countryCode1,LookupDto.class)).thenReturn(lookupDto1);
        when(modelMapper.map(countryCode2,LookupDto.class)).thenReturn(lookupDto2);

        //Act
        List<LookupDto> getCountryCodes= lookupServiceImpl.getCountryCodes();

        //Assert
        assertEquals(lookupDtos,getCountryCodes);
    }

    @Test
    public void testGetAdministrativeGenderCodes(){
        //Arrange
        AdministrativeGenderCode genderCode1=mock(AdministrativeGenderCode.class);
        AdministrativeGenderCode genderCode2=mock(AdministrativeGenderCode.class);

        List<AdministrativeGenderCode> genderCodes=new ArrayList<>();
        genderCodes.add(genderCode1);
        genderCodes.add(genderCode2);

        LookupDto lookupDto1=mock(LookupDto.class);
        LookupDto lookupDto2=mock(LookupDto.class);

        List<LookupDto> lookupDtos=new ArrayList<>();
        lookupDtos.add(lookupDto1);
        lookupDtos.add(lookupDto2);

        when(administrativeGenderCodeRepository.findAll()).thenReturn(genderCodes);

        when(modelMapper.map(genderCode1,LookupDto.class)).thenReturn(lookupDto1);
        when(modelMapper.map(genderCode2,LookupDto.class)).thenReturn(lookupDto2);

        //Act
        List<LookupDto> getGenderCodes=lookupServiceImpl.getAdministrativeGenderCodes();

        //Assert
        assertEquals(lookupDtos,getGenderCodes);
    }

    @Test
    public void testGetRoles(){
        //Arrange
        Role role1=mock(Role.class);
        Role role2=mock(Role.class);

        List<Role> roles=new ArrayList<>();
        roles.add(role1);
        roles.add(role2);

        RoleDto roleDto1=mock(RoleDto.class);
        RoleDto roleDto2=mock(RoleDto.class);

        List<RoleDto> roleDtos=new ArrayList<>();
        roleDtos.add(roleDto1);
        roleDtos.add(roleDto2);

        when(roleRepository.findAll()).thenReturn(roles);

        when(modelMapper.map(role1,RoleDto.class)).thenReturn(roleDto1);
        when(modelMapper.map(role2,RoleDto.class)).thenReturn(roleDto2);

        //Act
        List<RoleDto> getRoles=lookupServiceImpl.getRoles();

        //Assert
        assertEquals(roleDtos,getRoles);

    }



}

