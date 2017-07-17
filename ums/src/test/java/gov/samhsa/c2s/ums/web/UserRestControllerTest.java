package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.UserService;
import gov.samhsa.c2s.ums.service.dto.AccessDecisionDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserRestControllerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    StringTokenizer tokenizer;
    @Mock
    private UserService userServiceMock;
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
        //Arrange
        UserDto userDto = mock(UserDto.class);

        //Act
        sut.registerUser(userDto);

        //Assert
        verify(userServiceMock).registerUser(userDto);
    }

    @Test
    public void testEnableUser() {
        //Arrange
        Long userId = 20L;

        //Act
        sut.enableUser(userId, null);

        //Assert
        verify(userServiceMock).enableUser(userId, null);
    }

    @Test
    public void testDisableUser() {
        //Arrange
        Long userId = 20L;
        //Act
        sut.disableUser(userId, null);

        //Assert
        verify(userServiceMock).disableUser(userId, null);
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
        Long userId = 20L;
        UserDto userDto = mock(UserDto.class);
        when(userDto.getId()).thenReturn(userId);

        UserDto mockedUpdatedUserDto = mock(UserDto.class);
        when(mockedUpdatedUserDto.getId()).thenReturn(userId);

        when(userServiceMock.updateUser(anyLong(), any(UserDto.class))).thenReturn(mockedUpdatedUserDto);

        //Act
        UserDto updatedUserDto = sut.updateUser(userId, userDto);

        //Assert
        assertEquals(userId, updatedUserDto.getId());
        verify(userServiceMock).updateUser(userId, userDto);
    }

    @Test
    public void testGetUser() {
        //Arrange
        Long userId = 20L;
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
        Page<UserDto> page1 = mock(Page.class);

        when(userServiceMock.getAllUsers(page, size, null)).thenReturn(page1);

        //Act
        Page<UserDto> page2 = sut.getAllUsers(page, size, null);

        //Assert
        assertEquals(page1, page2);
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

        /*List<UserDto> list = new ArrayList<>();
        when(userServiceMock.searchUsersByDemographic(firstName, lastName, birthDate, genderCode)).thenReturn(list);

        //Act
        List<UserDto> list2 = sut.searchUsersByDemographic(firstName, lastName, birthDate, genderCode);

        //Assert
        assertEquals(list, list2);*/
    }

}