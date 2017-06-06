package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.Address;
import gov.samhsa.c2s.ums.domain.AddressRepository;
import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.domain.PatientRepository;
import gov.samhsa.c2s.ums.domain.Telecom;
import gov.samhsa.c2s.ums.domain.TelecomRepository;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserPatientRelationship;
import gov.samhsa.c2s.ums.domain.UserPatientRelationshipRepository;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCodeRepository;
import gov.samhsa.c2s.ums.domain.valueobject.UserPatientRelationshipId;
import gov.samhsa.c2s.ums.infrastructure.ScimService;
import gov.samhsa.c2s.ums.service.dto.AddressDto;
import gov.samhsa.c2s.ums.service.dto.GetUserResponseDto;
import gov.samhsa.c2s.ums.service.dto.RelationDto;
import gov.samhsa.c2s.ums.service.dto.RoleDto;
import gov.samhsa.c2s.ums.service.dto.TelecomDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import gov.samhsa.c2s.ums.web.UserRestController;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;

import javax.management.relation.Relation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import static org.mockito.Mockito.mock;

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

    @InjectMocks
    UserServiceImpl userServiceImpl;


    @Test
    public void testDisableUser_WhenUserIsFoundById() {
        //Arrange
        User user = mock(User.class);
        String id = "id";

        when(user.getOauth2UserId()).thenReturn(id);

        when(userRepository.findOneByIdAndIsDisabled(userId, false)).thenReturn(Optional.ofNullable(user));

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

        when(user.getOauth2UserId()).thenReturn(id);

        when(userRepository.findOneByIdAndIsDisabled(userId,false)).thenReturn(Optional.empty());

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

        when(user.getOauth2UserId()).thenReturn(id);

        when(userRepository.findOneByIdAndIsDisabled(userId, true)).thenReturn(Optional.ofNullable(user));

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

        when(user.getOauth2UserId()).thenReturn(id);

        when(userRepository.findOneByIdAndIsDisabled(userId, true)).thenReturn(Optional.empty());

        when(userRepository.save(user)).thenReturn(user);

        //act
        userServiceImpl.enableUser(userId);

        //assert
        verify(userRepository).save(user);

    }



    @Test
    public void testGetUser_WhoCanBeFoundByIdAndIsNotDisabled() {
        GetUserResponseDto getUserResponseDto = mock(GetUserResponseDto.class);
        Long userId = 10L;

        User user = mock(User.class);

        when(userRepository.findOneByIdAndIsDisabled(userId, false)).thenReturn(Optional.ofNullable(user));

        when(modelMapper.map(user, GetUserResponseDto.class)).thenReturn(getUserResponseDto);

        //Act
        GetUserResponseDto getUserResponseDto1= (GetUserResponseDto) userServiceImpl.getUser(userId);

        //assert
        assertEquals(getUserResponseDto, getUserResponseDto1);

    }

    @Test
    public void testGetUser_WhoCannotBefoundById_throwsException() {
        thrown.expect(UserNotFoundException.class);
        thrown.expectMessage("User Not Found!");
        GetUserResponseDto getUserResponseDto = mock(GetUserResponseDto.class);
        Long userId = 10L;

        User user = mock(User.class);

        when(userRepository.findOneByIdAndIsDisabled(userId, false)).thenReturn(Optional.empty());


        //act
        GetUserResponseDto getUserResponseDto1= (GetUserResponseDto) userServiceImpl.getUser(userId);

        //assert
        assertNull(getUserResponseDto1);
    }


    @Test
    public void testGetUserByOAuth2Id() {
        GetUserResponseDto getUserResponseDto = mock(GetUserResponseDto.class);
        String oAuth2UserId = "userId";

        User user = mock(User.class);

        when(userRepository.findOneByOauth2UserIdAndIsDisabled(oAuth2UserId, false)).thenReturn(Optional.ofNullable(user));

        when(modelMapper.map(user, GetUserResponseDto.class)).thenReturn(getUserResponseDto);

        //Act and Assert
        assertEquals(getUserResponseDto, userServiceImpl.getUserByOAuth2Id(oAuth2UserId));

    }


    @Test
    public void testSearchUsersByDemographic_whereThereIsUserOnTheUserList() {
        String firstName="firstName";
        String lastName="lastName";
        LocalDate birthDate=LocalDate.now();
        String genderCode="genderCode";


        User user1=mock(User.class);
        User user2=mock(User.class);

        List<User> userList =new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        List<GetUserResponseDto> getUserResponseDtos=new ArrayList<>();

        GetUserResponseDto getUserResponseDto1=mock(GetUserResponseDto.class);
        GetUserResponseDto getUserResponseDto2=mock(GetUserResponseDto.class);

        AdministrativeGenderCode administrativeGenderCode=mock(AdministrativeGenderCode.class);

        when(administrativeGenderCodeRepository.findByCode(genderCode)).thenReturn(administrativeGenderCode);

        when(userRepository.findAllByFirstNameAndLastNameAndBirthDayAndAdministrativeGenderCodeAndIsDisabled(firstName,lastName,birthDate,administrativeGenderCode,false))
                .thenReturn(userList);

        when(modelMapper.map(user1,GetUserResponseDto.class)).thenReturn(getUserResponseDto1);
        when(modelMapper.map(user2,GetUserResponseDto.class)).thenReturn(getUserResponseDto2);


        getUserResponseDtos.add(getUserResponseDto1);
        getUserResponseDtos.add(getUserResponseDto2);

        //Act
        List<GetUserResponseDto> getUserResponseDtoList=userServiceImpl.searchUsersByDemographic(firstName,lastName,birthDate,genderCode);

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

        List<User> userList =new ArrayList<>();

        AdministrativeGenderCode administrativeGenderCode=mock(AdministrativeGenderCode.class);

        when(administrativeGenderCodeRepository.findByCode(genderCode)).thenReturn(administrativeGenderCode);

        when(userRepository.findAllByFirstNameAndLastNameAndBirthDayAndAdministrativeGenderCodeAndIsDisabled(firstName,lastName,birthDate,administrativeGenderCode,false))
                .thenReturn(userList);

        //Act
        List<GetUserResponseDto> list=userServiceImpl.searchUsersByDemographic(firstName,lastName,birthDate,genderCode);

        //Assert
        assertNull(list);
    }

}
