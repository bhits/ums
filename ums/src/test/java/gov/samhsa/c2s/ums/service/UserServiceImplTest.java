package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.Demographics;
import gov.samhsa.c2s.ums.domain.DemographicsRepository;
import gov.samhsa.c2s.ums.domain.Locale;
import gov.samhsa.c2s.ums.domain.LocaleRepository;
import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserPatientRelationship;
import gov.samhsa.c2s.ums.domain.UserPatientRelationshipRepository;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCodeRepository;
import gov.samhsa.c2s.ums.infrastructure.ScimService;
import gov.samhsa.c2s.ums.service.dto.AccessDecisionDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ScimService scimService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AdministrativeGenderCodeRepository administrativeGenderCodeRepository;

    @Mock
    private LocaleRepository localeRepository;

    @Mock
    private UmsProperties umsProperties;

    @Mock
    private UserPatientRelationshipRepository userPatientRelationshipRepository;

    @Mock
    private DemographicsRepository demographicsRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Before
    public void setUp() {
        localeRepository = mock(LocaleRepository.class);
        scimService = mock(ScimService.class);
        userServiceImpl = new UserServiceImpl(localeRepository, scimService);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDisableUser_Given_UserIsFoundById() {
        //Arrange
        Long userId = 10L;

        User user = mock(User.class);
        String id = "id";

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(user.getUserAuthId()).thenReturn(id);

        when(userRepository.findByIdAndDisabled(userId, false)).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(user)).thenReturn(user);

        //Act
        userServiceImpl.disableUser(userId, Optional.of(""));

        //Assert
        verify(userRepository).save(user);
    }

    @Test
    public void testDisableUser_Given_NoUserIsFoundById_Then_ThrowsException() {
        //Arrange
        thrown.expect(UserNotFoundException.class);
        thrown.expectMessage("User Not Found!");

        Long userId = 10L;

        User user = mock(User.class);
        String id = "id";

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(user.getUserAuthId()).thenReturn(id);

        when(userRepository.findByIdAndDisabled(userId, false)).thenReturn(Optional.empty());

        //Act
        userServiceImpl.disableUser(userId, Optional.of(""));

        //Assert
        //ExpectedException annotated by @rule is thrown;
    }

    @Test
    public void testEnableUser_Given_UserIsFoundByIdAndIsDisabled() {
        //Arrange
        Long userId = 10L;

        User user = mock(User.class);
        String id = "id";

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(user.getUserAuthId()).thenReturn(id);

        when(userRepository.findByIdAndDisabled(userId, true)).thenReturn(Optional.ofNullable(user));

        when(userRepository.save(user)).thenReturn(user);

        //Act
        userServiceImpl.enableUser(userId, Optional.of(""));

        //Assert
        verify(userRepository).save(user);
    }

    @Test
    public void testEnableUser_Given_UserIsNotFoundByIdOrNotDisabled_Then_ThrowsException() {
        //Arrange
        thrown.expect(UserNotFoundException.class);
        thrown.expectMessage("User Not Found!");
        Long userId = 10L;

        User user = mock(User.class);
        String id = "id";

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(user.getUserAuthId()).thenReturn(id);

        when(userRepository.findByIdAndDisabled(userId, true)).thenReturn(Optional.empty());

        when(userRepository.save(user)).thenReturn(user);

        //Act
        userServiceImpl.enableUser(userId, Optional.of(""));

        //Assert
        //ExpectedException annotated by @rule is thrown.
    }

    @Test
    public void testGetUser_Given_UserCanBeFoundByIdAndIsNotDisabled() {
        //Arrange
        UserDto getUserResponseDto = mock(UserDto.class);
        Long userId = 10L;

        User user = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));

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

        when(userRepository.findByUserAuthIdAndDisabled(oAuth2UserId, false)).thenReturn(Optional.ofNullable(user));

        when(modelMapper.map(user, UserDto.class)).thenReturn(getUserResponseDto);

        //Act
        UserDto userDto = userServiceImpl.getUserByUserAuthId(oAuth2UserId);

        //Assert
        assertEquals(getUserResponseDto, userDto);
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

        when(userRepository.findByUserAuthIdAndDisabled(userAuthId, false)).thenReturn(Optional.ofNullable(user));
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
        UmsProperties.Mrn mrn = mock(UmsProperties.Mrn.class);
        String codeSystem = "code";
        Demographics demographics = mock(Demographics.class);

        when(userRepository.findByUserAuthIdAndDisabled(userAuthId, false)).thenReturn(Optional.ofNullable(user));
        when(umsProperties.getMrn()).thenReturn(mrn);
        when(mrn.getCodeSystem()).thenReturn(codeSystem);
        when(demographicsRepository.findOneByIdentifiersValueAndIdentifiersIdentifierSystemSystem(patientMrn, codeSystem)).thenReturn(Optional.ofNullable(demographics));

        when(demographics.getPatient()).thenReturn(patient);
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


        PageRequest pageRequest = new PageRequest(0, 10);

        UmsProperties.Pagination pagination = mock(UmsProperties.Pagination.class);
        when(umsProperties.getPagination()).thenReturn(pagination);
        when(pagination.getMaxSize()).thenReturn(10);
        when(pagination.getDefaultSize()).thenReturn(10);


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

        when(demographics1.getUser()).thenReturn(user1);
        when(demographics2.getUser()).thenReturn(user2);
        when(modelMapper.map(user1, UserDto.class)).thenReturn(getUserResponseDto1);
        when(modelMapper.map(user2, UserDto.class)).thenReturn(getUserResponseDto2);

        getUserResponseDtos.add(getUserResponseDto1);
        getUserResponseDtos.add(getUserResponseDto2);

        AdministrativeGenderCode administrativeGenderCode = mock(AdministrativeGenderCode.class);

        when(administrativeGenderCodeRepository.findByCode(genderCode)).thenReturn(administrativeGenderCode);

        Page<Demographics> demographicsPage = new PageImpl<Demographics>(demographicsList);

        when(demographicsRepository.query(firstName, lastName, administrativeGenderCode, birthDate, null, null, pageRequest))
                .thenReturn(demographicsPage);

        //Act
        Page<UserDto> userDtos = userServiceImpl.searchUsersByDemographic(firstName, lastName, birthDate, genderCode, null, null, Optional.of(0), Optional.of(10));

        //Assert
        assertEquals(userDtos.getTotalElements(), 2);

    }

    @Test
    public void testSearchUsersByDemographic_Given_ThereIsNoUserOnUserList() {
        //Arrange
        String firstName = "firstName";
        String lastName = "lastName";
        LocalDate birthDate = LocalDate.now();
        String genderCode = "genderCode";

        PageRequest pageRequest = new PageRequest(0, 10);

        UmsProperties.Pagination pagination = mock(UmsProperties.Pagination.class);
        when(umsProperties.getPagination()).thenReturn(pagination);
        when(pagination.getMaxSize()).thenReturn(10);
        when(pagination.getDefaultSize()).thenReturn(10);

        List<Demographics> demographicsList = new ArrayList<>();
        AdministrativeGenderCode administrativeGenderCode = mock(AdministrativeGenderCode.class);

        when(administrativeGenderCodeRepository.findByCode(genderCode)).thenReturn(administrativeGenderCode);

        List<Demographics> demographics = new ArrayList<>();

        Page<Demographics> demographicsPage = new PageImpl<Demographics>(demographics);

        when(demographicsRepository.query(firstName, lastName, administrativeGenderCode, birthDate, null, null, pageRequest))
                .thenReturn(demographicsPage);

        //Act
        Page<UserDto> userDtos = userServiceImpl.searchUsersByDemographic(firstName, lastName, birthDate, genderCode, null, null, Optional.of(0), Optional.of(10));

        //Assert
        assertEquals(userDtos.getTotalElements(), 0);
    }

}
