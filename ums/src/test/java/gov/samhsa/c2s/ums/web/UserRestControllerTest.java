package gov.samhsa.c2s.ums.web;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.samhsa.c2s.ums.domain.Locale;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.service.UserService;
import gov.samhsa.c2s.ums.service.dto.AccessDecisionDto;
import gov.samhsa.c2s.ums.service.dto.AddressDto;
import gov.samhsa.c2s.ums.service.dto.TelecomDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class UserRestControllerTest {

    Long userId = 20L;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private UserService userServiceMock;

    @Mock
    StringTokenizer tokenizer;

    @Mock
    private UserDto userDto;

    @InjectMocks
    private UserRestController sut;

    @BeforeClass
    public static void before() {
    }

    @AfterClass
    public static void after() {
    }


    @Test
    public void testRegisterUser() {
        //Act
        sut.registerUser(userDto);

        //Assert
        verify(userServiceMock).registerUser(userDto);
    }

    @Test
    public void testEnableUser() {
        //Act
        sut.enableUser(userId);

        //Assert
        verify(userServiceMock).enableUser(userId);
    }

    @Test
    public void testDisableUser() {
        //Act
        sut.disableUser(userId);

        //Assert
        verify(userServiceMock).disableUser(userId);
    }

    @Test
    public void testAccessDecision() {
        //Arrange
        String userAuthId = "userAuthId";
        String patientMrn = "patientMrn";

        AccessDecisionDto access = mock(AccessDecisionDto.class);
        when(userServiceMock.accessDecision(userAuthId, patientMrn)).thenReturn(access);

        //Act
        AccessDecisionDto accessDecision = sut.accessDecision(userAuthId, patientMrn);

        //Assert
        assertEquals(access, accessDecision);
    }


    @Test
    public void testUpdateUser() {
        //Arrange
        UserDto userDto = mock(UserDto.class);

        //Act
        sut.updateUser(userId, userDto);

        //Assert
        verify(userServiceMock).updateUser(userId, userDto);
    }

    @Test
    public void testGetUser() {
        //Arrange
        UserDto user = mock(UserDto.class);
        when(userServiceMock.getUser(userId)).thenReturn(user);

        //Act
        UserDto userGot = sut.getUser(userId);

        //Assert
        assertEquals(user, userGot);
    }

    @Test
    public void testGetUserById() {
        //Arrange
        String userAuthId = "OAuth2UserId";
        UserDto userDto = mock(UserDto.class);
        when(userServiceMock.getUserByUserAuthId(userAuthId)).thenReturn(userDto);

        //Act
        UserDto userDto1 = sut.getUserById(userAuthId);

        //Assert
        assertEquals(userDto, userDto1);
    }

    @Test
    public void testGetAllUsers() {
        //Arrange
        Optional<Integer> page = Optional.of(1233);
        Optional<Integer> size = Optional.of(234);

        //Act
        sut.getAllUsers(page, size);

        //Assert
        verify(userServiceMock).getAllUsers(page, size);
    }

    @Test
    public void testSearchUsersByFirstNameAndOrLastName() {
        //Arrange
        String term = "term";
        List<UserDto> list = new ArrayList<>();

        when(userServiceMock.searchUsersByFirstNameAndORLastName(tokenizer)).thenReturn(list);

        //Act
        List<UserDto> list2 = sut.searchUsersByFirstNameAndORLastName(term);

        //Assert
        assertEquals(list, list2);
    }

    @Test
    public void testSearchUsersByDemographic() {
        //Arrange
        String firstName = "firstName";
        String lastName = "lastName";
        LocalDate birthDate = LocalDate.now();
        String genderCode = "genderCode";

        List<UserDto> list = new ArrayList<>();
        when(userServiceMock.searchUsersByDemographic(firstName, lastName, birthDate, genderCode)).thenReturn(list);

        //Act
        List<UserDto> list2 = sut.searchUsersByDemographic(firstName, lastName, birthDate, genderCode);

        //Assert
        assertEquals(list, list2);
    }

}