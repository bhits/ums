package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.LookupService;
import gov.samhsa.c2s.ums.service.dto.LookupDto;
import gov.samhsa.c2s.ums.service.dto.RoleDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LookupRestControllerTest {
    @Mock
    LookupService lookupService;

    @InjectMocks
    LookupRestController lookupRestController;

    @Test
    public void testGetLocales() {
        //Arrange
        List<LookupDto> list = new ArrayList<>();
        when(lookupService.getLocales()).thenReturn(list);

        //Act
        List<LookupDto> list1 = lookupRestController.getLocales();

        //Assert
        assertEquals(list, list1);
    }

    @Test
    public void testGetStateCodes() {
        //Arrange
        List<LookupDto> list = new ArrayList<>();
        when(lookupService.getStateCodes()).thenReturn(list);

        //Act
        List<LookupDto> list1 = lookupRestController.getStateCodes();

        //Assert
        assertEquals(list, list1);
    }

    @Test
    public void testGetCountryCodes() {
        //Arrange
        List<LookupDto> list = new ArrayList<>();
        when(lookupService.getCountryCodes()).thenReturn(list);

        //Act
        List<LookupDto> list2 = lookupRestController.getCountryCodes();

        //Assert
        assertEquals(list, list2);
    }

    @Test
    public void testGetAdministrativeGenderCodes() {
        //Arrange
        List<LookupDto> list = new ArrayList<>();
        when(lookupService.getAdministrativeGenderCodes()).thenReturn(list);

        //Act
        List<LookupDto> list2 = lookupRestController.getAdministrativeGenderCodes();

        //Assert
        assertEquals(list, list2);
    }

    @Test
    public void testGetRoles() {
        //Arrange
        List<RoleDto> list = new ArrayList<>();
        when(lookupService.getRoles()).thenReturn(list);

        //Act
        List<RoleDto> list2 = lookupRestController.getRoles();

        //Assert
        assertEquals(list, list2);
    }
}
