package gov.samhsa.c2s.ums.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.samhsa.c2s.ums.domain.PatientRepository;
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

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ModelMapper modelMapper;
    private ObjectMapper objectMapper;

    @Mock
    private PatientRepository patientRepositoryMock;

    @InjectMocks
    private UserServiceImpl sut;

    @BeforeClass
    public static void before(){}

    @AfterClass
    public static void after(){}

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper();
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
    public void testCreateUser_Throws_Exception() throws Exception {
        //TODO:
    }

    @Test
    public void testUpdateUser() throws Exception {
        //TODO:
    }

    @Test
    public void testUpdateUser_Throws_Exception() throws Exception {
        //TODO:
    }

    @Test
    public void testDisableUser() throws Exception {
        //TODO:
    }

    @Test
    public void testGetUserByUserId() throws Exception {
        //TODO:
    }

    @Test
    public void testGetUserByUserId_Throws_UserNotFoundException() throws Exception {
        //TODO:
    }

    @Test
    public void testGetUserByOAuth2Id() throws Exception {
        //TODO:
    }

    @Test
    public void testGetUserByOAuth2Id_Throws_UserNotFoundException() throws Exception {
        //TODO:
    }

    @Test
    public void testGetAllUsers() throws Exception {
        //TODO:
    }

    @Test
    public void testGetAllUsers_Throws_UserNotFoundException() throws Exception {
        //TODO:
    }

    @Test
    public void testSearchUsersByFirstNameAndORLastName() throws Exception {
        //TODO:
    }

    @Test
    public void testSearchUsersByFirstNameAndORLastName_Throws_UserNotFoundException() throws Exception {
        //TODO:
    }

    @Test
    public void testSearchUsersByDemographic() throws Exception {
        //TODO:
    }

    @Test
    public void testSearchUsersByDemographic_Throws_UserNotFoundException() throws Exception {
        //TODO:
    }


}
