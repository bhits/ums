package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.Address;
import gov.samhsa.c2s.ums.domain.AddressRepository;
import gov.samhsa.c2s.ums.domain.Demographics;
import gov.samhsa.c2s.ums.domain.DemographicsRepository;
import gov.samhsa.c2s.ums.domain.Locale;
import gov.samhsa.c2s.ums.domain.LocaleRepository;
import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.domain.PatientRepository;
import gov.samhsa.c2s.ums.domain.Role;
import gov.samhsa.c2s.ums.domain.RoleRepository;
import gov.samhsa.c2s.ums.domain.Telecom;
import gov.samhsa.c2s.ums.domain.TelecomRepository;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserPatientRelationship;
import gov.samhsa.c2s.ums.domain.UserPatientRelationshipRepository;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCodeRepository;
import gov.samhsa.c2s.ums.infrastructure.ScimService;
import gov.samhsa.c2s.ums.service.dto.AccessDecisionDto;
import gov.samhsa.c2s.ums.service.dto.AddressDto;
import gov.samhsa.c2s.ums.service.dto.RelationDto;
import gov.samhsa.c2s.ums.service.dto.RoleDto;
import gov.samhsa.c2s.ums.service.dto.TelecomDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import gov.samhsa.c2s.ums.service.fhir.FhirPatientService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    Patient patient;

    @Mock
    ScimService scimService;

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @Mock
    MrnService mrnService;

    @Mock
    AdministrativeGenderCodeRepository administrativeGenderCodeRepository;

    @Mock
    PatientRepository patientRepository;

    @Mock
    TelecomRepository telecomRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    AddressRepository addressRepository;

    @Mock
    LocaleRepository localeRepository;

    @Mock
    UmsProperties umsProperties;

    @Mock
    RelationDto relationDto;
    @Mock
    UserPatientRelationshipRepository userPatientRelationshipRepository;

    @Mock
    DemographicsRepository demographicsRepository;

    @Mock
    FhirPatientService fhirPatientService;

    @InjectMocks
    UserServiceImpl userServiceImpl;

    @Test
    public void testRegisterUser() {
        //Arrange
        final String mrn = "mrn";
        Long userId = 30L;
        Long patientId = 20L;
        User user = mock(User.class);
        UserDto userDto = mock(UserDto.class);
        Demographics demographics = mock(Demographics.class);

        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(user.getDemographics()).thenReturn(demographics);

        TelecomDto telecomDto1 = mock(TelecomDto.class);
        TelecomDto telecomDto2 = mock(TelecomDto.class);
        List<TelecomDto> telecomDtos = new ArrayList<>();
        telecomDtos.add(telecomDto1);
        telecomDtos.add(telecomDto2);

        Telecom telecom1 = mock(Telecom.class);
        Telecom telecom2 = mock(Telecom.class);
        List<Telecom> telecoms = new ArrayList<>();
        telecoms.add(telecom1);
        telecoms.add(telecom2);

        AddressDto addressDto1 = mock(AddressDto.class);
        AddressDto addressDto2 = mock(AddressDto.class);

        List<AddressDto> addressDtos = new ArrayList<>();
        addressDtos.add(addressDto1);
        addressDtos.add(addressDto2);

        Address address1 = mock(Address.class);
        Address address2 = mock(Address.class);

        List<Address> addresses = new ArrayList<>();
        addresses.add(address1);
        addresses.add(address2);

        when(userDto.getTelecoms()).thenReturn(telecomDtos);
        when(modelMapper.map(telecomDtos, new TypeToken<List<Telecom>>() {
        }.getType())).thenReturn(telecoms);

        when(demographics.getTelecoms()).thenReturn(telecoms);

        when(userDto.getAddresses()).thenReturn(addressDtos);
        when(modelMapper.map(addressDtos, new TypeToken<List<Address>>() {
        }.getType())).thenReturn(addresses);

        when(demographics.getAddresses()).thenReturn(addresses);

        when(userRepository.save(user)).thenReturn(user);

        RoleDto roleDto1 = mock(RoleDto.class);
        List<RoleDto> roleDtos = new ArrayList<>();
        roleDtos.add(roleDto1);

        when(userDto.getRoles()).thenReturn(roleDtos);
        when(roleDto1.getCode()).thenReturn("patient");

        Patient patient = new Patient();

        when(mrnService.generateMrn()).thenReturn(mrn);
        patient.setMrn(mrn);
        patient.setDemographics(demographics);
        patient.setId(patientId);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        RelationDto relationDto = mock(RelationDto.class);
        UserPatientRelationship userPatientRelationship = mock(UserPatientRelationship.class);
        when(user.getId()).thenReturn(userId);

        when(modelMapper.map(relationDto, UserPatientRelationship.class)).thenReturn(userPatientRelationship);

        UmsProperties.Fhir fhir = mock(UmsProperties.Fhir.class);
        UmsProperties.Fhir.Publish publish = mock(UmsProperties.Fhir.Publish.class);

        when(umsProperties.getFhir()).thenReturn(fhir);
        when(fhir.getPublish()).thenReturn(publish);
        when(publish.isEnabled()).thenReturn(true);

        //Act
        userServiceImpl.registerUser(userDto);

        //Assert
        verify(userRepository).save(user);
        verify(userDto).setMrn(mrn);
        verify(fhirPatientService).publishFhirPatient(userDto);
    }

    @Test
    public void testDisableUser_Given_UserIsFoundById() {
        //Arrange
        Long userId = 10L;

        User user = mock(User.class);
        String id = "id";

        when(userRepository.findOne(userId)).thenReturn(user);
        when(user.getUserAuthId()).thenReturn(id);

        when(userRepository.findOneByIdAndDisabled(userId, false)).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(user)).thenReturn(user);

        //Act
        userServiceImpl.disableUser(userId);

        //Assert
        verify(userRepository).save(user);
    }

    @Test
    public void testDisableUser_Given_NoUserIsFoundById_Then_ThrowsException() throws UserNotFoundException {
        //Arrange
        thrown.expect(UserNotFoundException.class);
        thrown.expectMessage("User Not Found!");

        Long userId = 10L;

        User user = mock(User.class);
        String id = "id";

        when(userRepository.findOne(userId)).thenReturn(user);
        when(user.getUserAuthId()).thenReturn(id);

        when(userRepository.findOneByIdAndDisabled(userId, false)).thenReturn(Optional.empty());

        //Act
        userServiceImpl.disableUser(userId);

        //Assert
        verify(userRepository).save(user);
    }

    @Test
    public void testEnableUser_Given_UserIsFoundByIdAndIsDisabled() {
        //Arrange
        Long userId = 10L;

        User user = mock(User.class);
        String id = "id";

        when(userRepository.findOne(userId)).thenReturn(user);
        when(user.getUserAuthId()).thenReturn(id);

        when(userRepository.findOneByIdAndDisabled(userId, true)).thenReturn(Optional.ofNullable(user));

        when(userRepository.save(user)).thenReturn(user);

        //Act
        userServiceImpl.enableUser(userId);

        //Assert
        verify(userRepository).save(user);
    }

    @Test
    public void testEnableUser_Given_UserIsNotFoundByIdOrNotDisabled_Then_ThrowsException() throws UserNotFoundException {
        //Arrange
        thrown.expect(UserNotFoundException.class);
        thrown.expectMessage("User Not Found!");
        Long userId = 10L;

        User user = mock(User.class);
        String id = "id";

        when(userRepository.findOne(userId)).thenReturn(user);
        when(user.getUserAuthId()).thenReturn(id);


        when(userRepository.findOneByIdAndDisabled(userId, true)).thenReturn(Optional.empty());

        when(userRepository.save(user)).thenReturn(user);

        //Act
        userServiceImpl.enableUser(userId);

        //Assert
        verify(userRepository).save(user);
    }

    @Test
    public void testGetUser_Given_UserCanBeFoundByIdAndIsNotDisabled() {
        //Arrange
        UserDto getUserResponseDto = mock(UserDto.class);
        Long userId = 10L;

        User user = mock(User.class);

        when(userRepository.findOne(userId)).thenReturn(user);

        when(modelMapper.map(user, UserDto.class)).thenReturn(getUserResponseDto);

        //Act
        UserDto getUserResponseDto1 = userServiceImpl.getUser(userId);

        //Assert
        assertEquals(getUserResponseDto, getUserResponseDto1);
    }

    @Test
    public void testGetUserByUserAuthId() {
        //Arrange
        UserDto getUserResponseDto = mock(UserDto.class);
        String oAuth2UserId = "userId";

        User user = mock(User.class);

        when(userRepository.findOneByUserAuthIdAndDisabled(oAuth2UserId, false)).thenReturn(Optional.ofNullable(user));

        when(modelMapper.map(user, UserDto.class)).thenReturn(getUserResponseDto);

        //Act and Assert
        assertEquals(getUserResponseDto, userServiceImpl.getUserByUserAuthId(oAuth2UserId));
    }

    @Test
    public void testUpdateUserLocale() {
        //Arrange
        Long userId = 30L;
        String localeCode = "localCode";
        User user = mock(User.class);
        Locale locale = mock(Locale.class);

        when(userRepository.findOne(userId)).thenReturn(user);
        when(localeRepository.findByCode(localeCode)).thenReturn(locale);

        //Act
        userServiceImpl.updateUserLocale(userId, localeCode);

        //Assert
        verify(userRepository).save(user);
    }

    @Test
    public void testUpdateUserLocaleByUserAuthId() {
        //Arrange
        String userAuthId = "userAuthId";
        String localeCode = "localeCode";
        Locale locale = mock(Locale.class);
        User user = mock(User.class);

        when(userRepository.findOneByUserAuthIdAndDisabled(userAuthId, false)).thenReturn(Optional.ofNullable(user));
        when(localeRepository.findByCode(localeCode)).thenReturn(locale);

        //Act
        userServiceImpl.updateUserLocaleByUserAuthId(userAuthId, localeCode);

        //Assert
        verify(userRepository).save(user);
    }

    @Test
    public void testAccessDecision() {
        //Arrange
        String userAuthId = "userAuthId";
        String patientMrn = "patientMrn";
        Long userId = 30l;
        Long patientId = 20l;
        User user = mock(User.class);
        Patient patient = mock(Patient.class);

        when(userRepository.findOneByUserAuthIdAndDisabled(userAuthId, false)).thenReturn(Optional.ofNullable(user));
        when(patientRepository.findOneByMrn(patientMrn)).thenReturn(Optional.ofNullable(patient));

        UserPatientRelationship userPatientRelationship1 = mock(UserPatientRelationship.class);
        UserPatientRelationship userPatientRelationship2 = mock(UserPatientRelationship.class);
        List<UserPatientRelationship> userPatientRelationships = new ArrayList<>();
        userPatientRelationships.add(userPatientRelationship1);
        userPatientRelationships.add(userPatientRelationship2);

        when(user.getId()).thenReturn(userId);
        when(patient.getId()).thenReturn(patientId);
        when(userPatientRelationshipRepository.findAllByIdUserIdAndIdPatientId(userId, patientId)).thenReturn(userPatientRelationships);

        //Act
        AccessDecisionDto accessDecisionDto = userServiceImpl.accessDecision(userAuthId, patientMrn);

        //Assert
        assertEquals(new AccessDecisionDto(true), accessDecisionDto);
    }

    @Test
    public void testSearchUsersByDemographic_Given_ThereIsUserOnTheUserList() {
        //Arrange
        String firstName = "firstName";
        String lastName = "lastName";
        LocalDate birthDate = LocalDate.now();
        String genderCode = "genderCode";

        Demographics demographics1 = mock(Demographics.class);
        Demographics demographics2 = mock(Demographics.class);

        User user1 = mock(User.class);
        User user2 = mock(User.class);

        List<Demographics> demographicsList = new ArrayList<>();
        demographicsList.add(demographics1);
        demographicsList.add(demographics2);

        List<UserDto> getUserResponseDtos = new ArrayList<>();

        UserDto getUserResponseDto1 = mock(UserDto.class);
        UserDto getUserResponseDto2 = mock(UserDto.class);

        AdministrativeGenderCode administrativeGenderCode = mock(AdministrativeGenderCode.class);

        when(administrativeGenderCodeRepository.findByCode(genderCode)).thenReturn(administrativeGenderCode);

        when(demographicsRepository.findAllByFirstNameAndLastNameAndBirthDayAndAdministrativeGenderCode(firstName, lastName, birthDate, administrativeGenderCode))
                .thenReturn(demographicsList);

        when(demographics1.getUser()).thenReturn(user1);
        when(demographics2.getUser()).thenReturn(user2);
        when(modelMapper.map(user1, UserDto.class)).thenReturn(getUserResponseDto1);
        when(modelMapper.map(user2, UserDto.class)).thenReturn(getUserResponseDto2);

        getUserResponseDtos.add(getUserResponseDto1);
        getUserResponseDtos.add(getUserResponseDto2);

        //Act
        List<UserDto> getUserResponseDtoList = userServiceImpl.searchUsersByDemographic(firstName, lastName, birthDate, genderCode);

        //Assert
        assertEquals(getUserResponseDtos, getUserResponseDtoList);
    }

    @Test
    public void testSearchUsersByDemographic_Given_ThereIsNoUserOnUserList_Then_ThrowsException() throws UserNotFoundException {
        //Arrange
        thrown.expect(UserNotFoundException.class);
        thrown.expectMessage("User Not Found!");
        String firstName = "firstName";
        String lastName = "lastName";
        LocalDate birthDate = LocalDate.now();
        String genderCode = "genderCode";

        List<Demographics> demographicsList = new ArrayList<>();
        AdministrativeGenderCode administrativeGenderCode = mock(AdministrativeGenderCode.class);

        when(administrativeGenderCodeRepository.findByCode(genderCode)).thenReturn(administrativeGenderCode);

        when(demographicsRepository.findAllByFirstNameAndLastNameAndBirthDayAndAdministrativeGenderCode(firstName, lastName, birthDate, administrativeGenderCode))
                .thenReturn(demographicsList);

        //Act
        List<UserDto> list = userServiceImpl.searchUsersByDemographic(firstName, lastName, birthDate, genderCode);

        //Assert
        assertNull(list);
    }

}
