package gov.samhsa.c2s.ums.web;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.service.UserService;
import gov.samhsa.c2s.ums.service.dto.AddressDto;
import gov.samhsa.c2s.ums.service.dto.TelecomDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
import java.util.StringTokenizer;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@Ignore
@RunWith(MockitoJUnitRunner.class)
public class UserRestControllerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObjectMapper objectMapper;
    private ModelMapper modelMapper;
    private MockMvc mvc;

    @Mock
    private UserService userServiceMock;

    @InjectMocks
    private UserRestController sut;

    @BeforeClass
    public static void before(){}

    @AfterClass
    public static void after(){}

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        mvc = MockMvcBuilders.standaloneSetup(this.sut).build();
        modelMapper = new ModelMapper();
    }

    @After
    public void tearDown(){

    }

    @Test
    public void testCreateUser() throws Exception {
        //TODO:
    }

    @Test
    public void testUpdateUser() throws Exception {
        //TODO:
    }

    @Test
    public void testDisableUser() throws Exception {
        //TODO:
    }

    @Test
    public void testGetUserByUserId() throws Exception {
        // Arrange
        TelecomDto emailDto = TelecomDto.builder().system("email").value("alice.recruit@mailinator.com").build();
        TelecomDto phoneDto = TelecomDto.builder().system("phone").value("1234567890").build();
        AddressDto addressDto = AddressDto.builder()
                .streetAddressLine("1111 Main Street")
                .city("Columbia")
                .stateCode("MD")
                .postalCode("22222")
                .countryCode("US")
                .build();
        final int year = 2010;
        final int month = 2;
        final int day = 3;

        UserDto userOne =  UserDto.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Recruit")
                .birthDate(LocalDate.of(year, month, day))
                .genderCode("female")
                .socialSecurityNumber("123456789")
                .address(addressDto)
                .telecom(Arrays.asList(emailDto, phoneDto))
                .build();

        when(userServiceMock.getUser(anyLong())).thenReturn(userOne);

        // Act and Assert
        mvc.perform(get("/users/" + 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.lastName", is(userOne.getLastName())))
                .andExpect(jsonPath("$.firstName", is(userOne.getFirstName())))
                .andExpect(jsonPath("$.birthDate.[0]", is(userOne.getBirthDate().getYear())))
                //Todo: Validate month
                //.andExpect(jsonPath("$.birthDate.month", is(userOne.getBirthDate().getMonth().name())))
                .andExpect(jsonPath("$.birthDate.[2]", is(userOne.getBirthDate().getDayOfMonth())))
                .andExpect(jsonPath("$.genderCode", is(userOne.getGenderCode())))
                .andExpect(jsonPath("$.socialSecurityNumber", is(userOne.getSocialSecurityNumber())))
                .andExpect(jsonPath("$.address.streetAddressLine", is(userOne.getAddress().getStreetAddressLine())))
                .andExpect(jsonPath("$.address.city", is(userOne.getAddress().getCity())))
                .andExpect(jsonPath("$.address.stateCode", is(userOne.getAddress().getStateCode())))
                .andExpect(jsonPath("$.address.postalCode", is(userOne.getAddress().getPostalCode())))
                .andExpect(jsonPath("$.address.countryCode", is(userOne.getAddress().getCountryCode())))
                .andExpect(jsonPath("$.telecom").value(hasSize(2)))
                .andExpect(jsonPath("$.telecom.[0].system", is(anyOf( equalTo("email"),
                        equalTo("phone")))))
                .andExpect(jsonPath("$.telecom.[0].value", is(anyOf( equalTo("alice.recruit@mailinator.com"),
                        equalTo("1234567890")))))
                .andExpect(jsonPath("$.telecom.[1].system", is(anyOf( equalTo("email"),
                        equalTo("phone")))))
                .andExpect(jsonPath("$.telecom.[1].value", is(anyOf( equalTo("alice.recruit@mailinator.com"),
                        equalTo("1234567890")))));
    }

    @Test
    public void testGetUserByUserId_Throws_UserNotFoundException() throws Exception {
        // Arrange
        //thrown.expect(UserNotFoundException.class);
        when(userServiceMock.getUser(anyLong())).thenThrow(new UserNotFoundException("User Not Found!"));

        // Act and Assert
        mvc.perform(get("/users/" + 1L))
                .andExpect(status().isNotFound());
        verify(userServiceMock, times(1)).getUser(anyLong());
        verifyNoMoreInteractions(userServiceMock);
        //thrown.expect(UserNotFoundException.class);
    }

    @Test
    public void testGetUserByOauth2UserId() throws Exception {
        // Arrange
        TelecomDto emailDto = TelecomDto.builder().system("email").value("alice.recruit@mailinator.com").build();
        TelecomDto phoneDto = TelecomDto.builder().system("phone").value("1234567890").build();
        AddressDto addressDto = AddressDto.builder()
                .streetAddressLine("1111 Main Street")
                .city("Columbia")
                .stateCode("MD")
                .postalCode("22222")
                .countryCode("US")
                .build();
        final int year = 2010;
        final int month = 2;
        final int day = 3;

        UserDto userOne =  UserDto.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Recruit")
                .birthDate(LocalDate.of(year, month, day))
                .genderCode("female")
                .socialSecurityNumber("123456789")
                .address(addressDto)
                .telecom(Arrays.asList(emailDto, phoneDto))
                .build();

        when(userServiceMock.getUserByOAuth2Id(anyString())).thenReturn(userOne);

        // Act and Assert
        mvc.perform(get("/users/OAuth2/" + "authId-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.lastName", is(userOne.getLastName())))
                .andExpect(jsonPath("$.firstName", is(userOne.getFirstName())))
                //Todo: Validate birthDate
                //.andExpect(jsonPath("$.birthDate.year", is(userOne.getBirthDate().getYear())))
                //.andExpect(jsonPath("$.birthDate.month", is(userOne.getBirthDate().getMonth().name())))
                //.andExpect(jsonPath("$.birthDate.dayOfMonth", is(userOne.getBirthDate().getDayOfMonth())))
                .andExpect(jsonPath("$.genderCode", is(userOne.getGenderCode())))
                .andExpect(jsonPath("$.socialSecurityNumber", is(userOne.getSocialSecurityNumber())))
                .andExpect(jsonPath("$.address.streetAddressLine", is(userOne.getAddress().getStreetAddressLine())))
                .andExpect(jsonPath("$.address.city", is(userOne.getAddress().getCity())))
                .andExpect(jsonPath("$.address.stateCode", is(userOne.getAddress().getStateCode())))
                .andExpect(jsonPath("$.address.postalCode", is(userOne.getAddress().getPostalCode())))
                .andExpect(jsonPath("$.address.countryCode", is(userOne.getAddress().getCountryCode())))
                .andExpect(jsonPath("$.telecom").value(hasSize(2)))
                .andExpect(jsonPath("$.telecom.[0].system", is(anyOf( equalTo("email"),
                        equalTo("phone")))))
                .andExpect(jsonPath("$.telecom.[0].value", is(anyOf( equalTo("alice.recruit@mailinator.com"),
                        equalTo("1234567890")))))
                .andExpect(jsonPath("$.telecom.[1].system", is(anyOf( equalTo("email"),
                        equalTo("phone")))))
                .andExpect(jsonPath("$.telecom.[1].value", is(anyOf( equalTo("alice.recruit@mailinator.com"),
                        equalTo("1234567890")))));
    }

    @Test
    public void testGetUserByOauth2UserId_Throws_UserNotFoundException() throws Exception {
        // Arrange
        //thrown.expect(UserNotFoundException.class);
        when(userServiceMock.getUserByOAuth2Id(anyString())).thenThrow(new UserNotFoundException("User Not Found!"));

        // Act and Assert
        mvc.perform(get("/users/OAuth2/" + "authId-001"))
                .andExpect(status().isNotFound());
        verify(userServiceMock, times(1)).getUserByOAuth2Id(anyString());
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    //@Ignore
    public void testGetAllUsers() throws Exception {
        // Arrange
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(createSampleUserDtoOne());
        userDtoList.add(createSampleUserDtoTwo());
        Page<UserDto> newPage = new PageImpl<>(userDtoList);

        when(userServiceMock.getAllUsers(any(), any())).thenReturn(newPage);

        // Act and Assert
        mvc.perform(get("/users/search;page=5;size=10" ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.numberOfElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.first", is(true)));
    }

    @Test
    public void testSearchUsersByFirstNameAndORLastName() throws Exception {
        // Arrange
        List<UserDto> userDtoList = new ArrayList<>();
        UserDto userDto = createSampleUserDtoOne();
        userDtoList.add(userDto);

        when(userServiceMock.searchUsersByFirstNameAndORLastName(new StringTokenizer(anyString()))).thenReturn(userDtoList);

        // Act and Assert
        mvc.perform(get("/users/search/" + new StringTokenizer("Alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$.[0].lastName", is(userDto.getLastName())))
                .andExpect(jsonPath("$.[0].firstName", is(userDto.getFirstName())))
                //Todo: Validate birthDate
                //.andExpect(jsonPath("$.birthDate.year", is(userDto.getBirthDate().getYear())))
                //.andExpect(jsonPath("$.birthDate.month", is(userDto.getBirthDate().getMonth().name())))
                //.andExpect(jsonPath("$.birthDate.dayOfMonth", is(userDto.getBirthDate().getDayOfMonth())))
                .andExpect(jsonPath("$.[0].genderCode", is(userDto.getGenderCode())))
                .andExpect(jsonPath("$.[0].socialSecurityNumber", is(userDto.getSocialSecurityNumber())))
                .andExpect(jsonPath("$.[0].address.streetAddressLine", is(userDto.getAddress().getStreetAddressLine())))
                .andExpect(jsonPath("$.[0].address.city", is(userDto.getAddress().getCity())))
                .andExpect(jsonPath("$.[0].address.stateCode", is(userDto.getAddress().getStateCode())))
                .andExpect(jsonPath("$.[0].address.postalCode", is(userDto.getAddress().getPostalCode())))
                .andExpect(jsonPath("$.[0].address.countryCode", is(userDto.getAddress().getCountryCode())))
                .andExpect(jsonPath("$.[0].telecom").value(hasSize(2)))
                .andExpect(jsonPath("$.[0].telecom.[0].system", is(anyOf( equalTo("email"),
                        equalTo("phone")))))
                .andExpect(jsonPath("$.[0].telecom.[0].value", is(anyOf( equalTo("alice.recruit@mailinator.com"),
                        equalTo("1234567890")))))
                .andExpect(jsonPath("$.[0].telecom.[1].system", is(anyOf( equalTo("email"),
                        equalTo("phone")))))
                .andExpect(jsonPath("$.[0].telecom.[1].value", is(anyOf( equalTo("alice.recruit@mailinator.com"),
                        equalTo("1234567890")))));

        ;
    }

    @Test
    public void testSearchUsersByDemographic() throws Exception {
        // Arrange
        List<UserDto> userDtoList = new ArrayList<>();
        UserDto userDto = createSampleUserDtoOne();
        userDtoList.add(userDto);

        when(userServiceMock.searchUsersByDemographic(anyString(), anyString(), any(LocalDate.class), anyString())).thenReturn(userDtoList);

        // Act and Assert
        mvc.perform(get("/users/search/patientDemographic?firstName=" + "Alice"
                                                                    +"&lastName=" + "Recruit"
                                                                    +"&birthDate=" + "1980-01-01"
                                                                    +"&genderCode=" + "female"

                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$.[0].lastName", is(userDto.getLastName())))
                .andExpect(jsonPath("$.[0].firstName", is(userDto.getFirstName())))
                //Todo: Validate birthDate
                //.andExpect(jsonPath("$.birthDate.year", is(userDto.getBirthDate().getYear())))
                //.andExpect(jsonPath("$.birthDate.month", is(userDto.getBirthDate().getMonth().name())))
                //.andExpect(jsonPath("$.birthDate.dayOfMonth", is(userDto.getBirthDate().getDayOfMonth())))
                .andExpect(jsonPath("$.[0].genderCode", is(userDto.getGenderCode())))
                .andExpect(jsonPath("$.[0].socialSecurityNumber", is(userDto.getSocialSecurityNumber())))
                .andExpect(jsonPath("$.[0].address.streetAddressLine", is(userDto.getAddress().getStreetAddressLine())))
                .andExpect(jsonPath("$.[0].address.city", is(userDto.getAddress().getCity())))
                .andExpect(jsonPath("$.[0].address.stateCode", is(userDto.getAddress().getStateCode())))
                .andExpect(jsonPath("$.[0].address.postalCode", is(userDto.getAddress().getPostalCode())))
                .andExpect(jsonPath("$.[0].address.countryCode", is(userDto.getAddress().getCountryCode())))
                .andExpect(jsonPath("$.[0].telecom").value(hasSize(2)))
                .andExpect(jsonPath("$.[0].telecom.[0].system", is(anyOf( equalTo("email"),
                        equalTo("phone")))))
                .andExpect(jsonPath("$.[0].telecom.[0].value", is(anyOf( equalTo("alice.recruit@mailinator.com"),
                        equalTo("1234567890")))))
                .andExpect(jsonPath("$.[0].telecom.[1].system", is(anyOf( equalTo("email"),
                        equalTo("phone")))))
                .andExpect(jsonPath("$.[0].telecom.[1].value", is(anyOf( equalTo("alice.recruit@mailinator.com"),
                        equalTo("1234567890")))));
                ;
    }

    private UserDto createSampleUserDtoOne() {

        TelecomDto emailDto = TelecomDto.builder().system("email").value("alice.recruit@mailinator.com").build();
        TelecomDto phoneDto = TelecomDto.builder().system("phone").value("1234567890").build();
        AddressDto addressDto = AddressDto.builder()
                .streetAddressLine("1111 Main Street")
                .city("Columbia")
                .stateCode("MD")
                .postalCode("22222")
                .countryCode("US")
                .build();
        final int year = 2010;
        final int month = 2;
        final int day = 3;

        return UserDto.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Recruit")
                .birthDate(LocalDate.of(year, month, day))
                .genderCode("female")
                .socialSecurityNumber("123456789")
                .address(addressDto)
                .telecom(Arrays.asList(emailDto,phoneDto))
                .build();
    }

    private UserDto createSampleUserDtoTwo() {

        TelecomDto emailDto = TelecomDto.builder().system("email").value("John.Doe@mailinator.com").build();
        TelecomDto phoneDto = TelecomDto.builder().system("phone").value("1234567891").build();
        AddressDto addressDto = AddressDto.builder()
                .streetAddressLine("1111 Main Street")
                .city("Columbia")
                .stateCode("MD")
                .countryCode("US")
                .postalCode("22222")
                .countryCode("US")
                .build();
        final int year = 2011;
        final int month = 4;
        final int day = 5;

        return UserDto.builder()
                .id(2L)
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(year, month, day))
                .genderCode("male")
                .address(addressDto)
                .socialSecurityNumber("987654321")
                .telecom(Arrays.asList(emailDto,phoneDto))
                .build();
    }

    private User createSampleUserOne() {
        UserDto userOneDto = createSampleUserDtoOne();
        User userOne = modelMapper.map(userOneDto,User.class);
        //set auth2UserId
        userOne.setOauth2UserId("auth1");
        return userOne;
    }

    private User createSampleUserTwo() {
        UserDto userTwoDto = createSampleUserDtoTwo();
        User userTwo = modelMapper.map(userTwoDto,User.class);
        //set auth2UserId
        userTwo.setOauth2UserId("auth2");
        return userTwo;
    }

    //Utility to print Object structure
    private void displayJSONStructure(Object obj)  throws JsonGenerationException, JsonMappingException, IOException {
        String jsonInString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        System.out.println("==========================");
        System.out.println(jsonInString);
        System.out.println("==========================");

    }


}
