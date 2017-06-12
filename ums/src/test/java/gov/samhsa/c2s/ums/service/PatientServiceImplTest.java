package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.domain.PatientRepository;
import gov.samhsa.c2s.ums.domain.Relationship;
import gov.samhsa.c2s.ums.domain.RelationshipRepository;
import gov.samhsa.c2s.ums.domain.Role;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserPatientRelationship;
import gov.samhsa.c2s.ums.domain.UserPatientRelationshipRepository;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.domain.valueobject.RelationshipRoleId;
import gov.samhsa.c2s.ums.domain.valueobject.UserPatientRelationshipId;
import gov.samhsa.c2s.ums.service.dto.PatientDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class PatientServiceImplTest {

    @Mock
    PatientRepository patientRepository;

    @Mock
    ModelMapper modelMapper;

    @Mock
    UserRepository userRepository;

    @Mock
    UserPatientRelationshipRepository userPatientRelationshipRepository;

    @Mock
    RelationshipRepository relationshipRepository;

    @InjectMocks
    PatientServiceImpl patientService;

    @Test
    public void testGetPatientByPatientId() {
        //Arrange
        String patientId = "patientId";
        Patient patient = mock(Patient.class);
        Optional<String> userAuthId = Optional.of("userId");
        Long id = 30L;
        Long pId = 20L;
        PatientDto patientDto = mock(PatientDto.class);

        when(patientRepository.findOneByMrn(patientId)).thenReturn(Optional.ofNullable(patient));

        User user = mock(User.class);
        when(userRepository.findOneByUserAuthIdAndDisabled(userAuthId.get(), false)).thenReturn(Optional.ofNullable(user));

        UserPatientRelationship userPatientRelationship1 = mock(UserPatientRelationship.class);
        UserPatientRelationship userPatientRelationship2 = mock(UserPatientRelationship.class);

        List<UserPatientRelationship> userPatientRelationships = new ArrayList<>();
        userPatientRelationships.add(userPatientRelationship1);
        userPatientRelationships.add(userPatientRelationship2);

        when(user.getId()).thenReturn(id);
        when(patient.getId()).thenReturn(pId);
        when(userPatientRelationshipRepository.findAllByIdUserIdAndIdPatientId(id, pId)).thenReturn(userPatientRelationships);

        when(modelMapper.map(patient, PatientDto.class)).thenReturn(patientDto);

        //Act
        PatientDto patientDto1 = patientService.getPatientByPatientId(patientId, userAuthId);

        //Assert
        assertEquals(patientDto, patientDto1);
    }

    @Test
    public void getPatientByUserAuthId() {
        //Arrange
        String userAuthId = "userAuthId";
        User user = mock(User.class);
        Long userId = 30L;

        UserPatientRelationship userPatientRelationship1 = mock(UserPatientRelationship.class);
        UserPatientRelationship userPatientRelationship2 = mock(UserPatientRelationship.class);
        List<UserPatientRelationship> userPatientRelationships = new ArrayList<>();
        userPatientRelationships.add(userPatientRelationship1);
        userPatientRelationships.add(userPatientRelationship2);

        when(userRepository.findOneByUserAuthIdAndDisabled(userAuthId, false)).thenReturn(Optional.ofNullable(user));

        when(user.getId()).thenReturn(userId);
        when(userPatientRelationshipRepository.findAllByIdUserId(userId)).thenReturn(userPatientRelationships);

        PatientDto patientDto1 = mock(PatientDto.class);
        PatientDto patientDto2 = mock(PatientDto.class);
        List<PatientDto> patientDtos = new ArrayList<>();


        UserPatientRelationshipId userPatientRelationshipId1 = mock(UserPatientRelationshipId.class);
        UserPatientRelationshipId userPatientRelationshipId2 = mock(UserPatientRelationshipId.class);

        when(userPatientRelationship1.getId()).thenReturn(userPatientRelationshipId1);
        when(userPatientRelationship2.getId()).thenReturn(userPatientRelationshipId2);

        Patient patient1 = mock(Patient.class);
        Patient patient2 = mock(Patient.class);
        when(userPatientRelationshipId1.getPatient()).thenReturn(patient1);
        when(userPatientRelationshipId2.getPatient()).thenReturn(patient2);

        when(modelMapper.map(patient1, PatientDto.class)).thenReturn(patientDto1);
        when(modelMapper.map(patient2, PatientDto.class)).thenReturn(patientDto2);

        Relationship relationship1 = mock(Relationship.class);
        Relationship relationship2 = mock(Relationship.class);

        when(userPatientRelationshipId1.getRelationship()).thenReturn(relationship1);
        when(userPatientRelationshipId2.getRelationship()).thenReturn(relationship2);

        RelationshipRoleId relationshipRoleId1 = mock(RelationshipRoleId.class);
        RelationshipRoleId relationshipRoleId2 = mock(RelationshipRoleId.class);
        when(relationship1.getId()).thenReturn(relationshipRoleId1);
        when(relationship2.getId()).thenReturn(relationshipRoleId2);

        Role role1 = mock(Role.class);
        Role role2 = mock(Role.class);
        when(relationshipRoleId1.getRole()).thenReturn(role1);
        when(relationshipRoleId2.getRole()).thenReturn(role2);

        String code1 = "code1";
        String code2 = "code2";
        when(role1.getCode()).thenReturn(code1);
        when(role2.getCode()).thenReturn(code2);


        patientDtos.add(patientDto1);
        patientDtos.add(patientDto2);

        //Act
        List<PatientDto> list = patientService.getPatientByUserAuthId(userAuthId);

        //Assert
        assertEquals(patientDtos, list);
    }
}
