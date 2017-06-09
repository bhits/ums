package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.PatientService;
import gov.samhsa.c2s.ums.service.dto.PatientDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PatientRestControllerTest {
    @Mock
    PatientService patientService;

    @InjectMocks
    PatientRestController patientRestController;

    @Test
    public void testGetPatientByPatientId() {
        //Arrange
        PatientDto patientDto = mock(PatientDto.class);
        String patientId = "patientId";
        Optional<String> userAuthId = Optional.of("userAuthId");
        when(patientService.getPatientByPatientId(patientId, userAuthId)).thenReturn(patientDto);

        //Act
        PatientDto patientDto1 = patientRestController.getPatientByPatientId(patientId, userAuthId);

        //Assert
        assertEquals(patientDto, patientDto1);
    }

    @Test
    public void testGetPatientByUserAuthId() {
        //Arrange
        List<PatientDto> list = new ArrayList<>();
        String userAuthId = "userAuthId";
        when(patientService.getPatientByUserAuthId(userAuthId)).thenReturn(list);

        //Act
        List<PatientDto> list1 = patientRestController.getPatientByUserAuthId(userAuthId);

        //Assert
        assertEquals(list, list1);
    }
}
