package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.AddressRepository;
import gov.samhsa.c2s.ums.domain.Demographics;
import gov.samhsa.c2s.ums.domain.DemographicsRepository;
import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.domain.PatientRepository;
import gov.samhsa.c2s.ums.domain.TelecomRepository;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserPatientRelationshipRepository;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCodeRepository;
import gov.samhsa.c2s.ums.infrastructure.ScimService;
import gov.samhsa.c2s.ums.service.dto.RelationDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private long userId = 34L;
    private long patientId = 30L;

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
    AddressRepository addressRepository;


    @Mock
    RelationDto relationDto;
    @Mock
    UserPatientRelationshipRepository userPatientRelationshipRepository;

    @Mock
    DemographicsRepository demographicsRepository;

    @InjectMocks
    UserServiceImpl userServiceImpl;


    @Test
    public void testDisableUser_WhenUserIsFoundById() {
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

        //assert
        verify(userRepository).save(user);

    }

    @Test
    public void testDisableUser_whenNoUserIsFoundById_throwsException() throws UserNotFoundException {
        thrown.expect(UserNotFoundException.class);
        thrown.expectMessage("User Not Found!");

        Long userId = 10L;

        User user = mock(User.class);
        String id = "id";

        when(userRepository.findOne(userId)).thenReturn(user);
        when(user.getUserAuthId()).thenReturn(id);

        when(userRepository.findOneByIdAndDisabled(userId,false)).thenReturn(Optional.empty());

        //Act
        userServiceImpl.disableUser(userId);

        //Assert
        verify(userRepository).save(user);


    }


    @Test
    public void testEnableUser_whenUserIsFoundByIdAndIsDisabled() {
        Long userId = 10L;

        User user = mock(User.class);
        String id = "id";

        when(userRepository.findOne(userId)).thenReturn(user);
        when(user.getUserAuthId()).thenReturn(id);

        when(userRepository.findOneByIdAndDisabled(userId, true)).thenReturn(Optional.ofNullable(user));

        when(userRepository.save(user)).thenReturn(user);

        //Act
        userServiceImpl.enableUser(userId);

        //assert
        verify(userRepository).save(user);

    }

    @Test
    public void testEnableUser_whenUserIsNotFoundByIdOrNotDisabled() throws UserNotFoundException {
        thrown.expect(UserNotFoundException.class);
        thrown.expectMessage("User Not Found!");
        Long userId = 10L;

        User user = mock(User.class);
        String id = "id";

        when(userRepository.findOne(userId)).thenReturn(user);
        when(user.getUserAuthId()).thenReturn(id);


        when(userRepository.findOneByIdAndDisabled(userId, true)).thenReturn(Optional.empty());

        when(userRepository.save(user)).thenReturn(user);

        //act
        userServiceImpl.enableUser(userId);

        //assert
        verify(userRepository).save(user);

    }



    @Test
    public void testGetUser_WhoCanBeFoundByIdAndIsNotDisabled() {
        UserDto getUserResponseDto = mock(UserDto.class);
        Long userId = 10L;

        User user = mock(User.class);

        when(userRepository.findOne(userId)).thenReturn(user);

        when(modelMapper.map(user, UserDto.class)).thenReturn(getUserResponseDto);

        //Act
        UserDto getUserResponseDto1=  userServiceImpl.getUser(userId);

        //assert
        assertEquals(getUserResponseDto, getUserResponseDto1);

    }


    @Test
    public void testGetUserByOAuth2Id() {
        UserDto getUserResponseDto = mock(UserDto.class);
        String oAuth2UserId = "userId";

        User user = mock(User.class);

        when(userRepository.findOneByUserAuthIdAndDisabled(oAuth2UserId, false)).thenReturn(Optional.ofNullable(user));

        when(modelMapper.map(user, UserDto.class)).thenReturn(getUserResponseDto);

        //Act and Assert
        assertEquals(getUserResponseDto, userServiceImpl.getUserByUserAuthId(oAuth2UserId));

    }


    @Test
    public void testSearchUsersByDemographic_whereThereIsUserOnTheUserList() {
        String firstName="firstName";
        String lastName="lastName";
        LocalDate birthDate=LocalDate.now();
        String genderCode="genderCode";


        Demographics demographics1=mock(Demographics.class);
        Demographics demographics2=mock(Demographics.class);

        User user1=mock(User.class);
        User user2=mock(User.class);

        List<Demographics> demographicsList =new ArrayList<>();
        demographicsList.add(demographics1);
        demographicsList.add(demographics2);


        List<UserDto> getUserResponseDtos=new ArrayList<>();

        UserDto getUserResponseDto1=mock(UserDto.class);
        UserDto getUserResponseDto2=mock(UserDto.class);

        AdministrativeGenderCode administrativeGenderCode=mock(AdministrativeGenderCode.class);

        when(administrativeGenderCodeRepository.findByCode(genderCode)).thenReturn(administrativeGenderCode);

        when(demographicsRepository.findAllByFirstNameAndLastNameAndBirthDayAndAdministrativeGenderCode(firstName,lastName,birthDate,administrativeGenderCode))
                .thenReturn(demographicsList);

        when(demographics1.getUser()).thenReturn(user1);
        when(demographics2.getUser()).thenReturn(user2);
        when(modelMapper.map(user1,UserDto.class)).thenReturn(getUserResponseDto1);
        when(modelMapper.map(user2,UserDto.class)).thenReturn(getUserResponseDto2);


        getUserResponseDtos.add(getUserResponseDto1);
        getUserResponseDtos.add(getUserResponseDto2);

        //Act
        List<UserDto> getUserResponseDtoList=userServiceImpl.searchUsersByDemographic(firstName,lastName,birthDate,genderCode);

        //Assert
        assertEquals(getUserResponseDtos,getUserResponseDtoList);
    }

    @Test
    public void testSearchUsersByDemographic_whenThereIsNoUserOnUserList_throwsException() throws UserNotFoundException {
        thrown.expect(UserNotFoundException.class);
        thrown.expectMessage("User Not Found!");
        String firstName="firstName";
        String lastName="lastName";
        LocalDate birthDate=LocalDate.now();
        String genderCode="genderCode";

        User user1=mock(User.class);
        User user2=mock(User.class);

        List<Demographics> demographicsList =new ArrayList<>();
        AdministrativeGenderCode administrativeGenderCode=mock(AdministrativeGenderCode.class);

        when(administrativeGenderCodeRepository.findByCode(genderCode)).thenReturn(administrativeGenderCode);

        when(demographicsRepository.findAllByFirstNameAndLastNameAndBirthDayAndAdministrativeGenderCode(firstName,lastName,birthDate,administrativeGenderCode))
                .thenReturn(demographicsList);

        //Act
        List<UserDto> list=userServiceImpl.searchUsersByDemographic(firstName,lastName,birthDate,genderCode);

        //Assert
        assertNull(list);
    }

}
