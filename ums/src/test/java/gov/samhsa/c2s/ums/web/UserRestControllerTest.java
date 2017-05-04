package gov.samhsa.c2s.ums.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.samhsa.c2s.ums.service.UserService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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


}
