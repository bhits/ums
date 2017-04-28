package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.domain.PatientRepository;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserPatientRelationship;
import gov.samhsa.c2s.ums.domain.UserPatientRelationshipRepository;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.service.dto.PatientDto;
import gov.samhsa.c2s.ums.service.exception.PatientNotFoundException;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

@Service
@Slf4j
public class PatientServiceImpl implements PatientService{

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final UserPatientRelationshipRepository userPatientRelationshipRepository;

    public PatientServiceImpl(PatientRepository patientRepository, ModelMapper modelMapper, UserRepository userRepository, UserPatientRelationshipRepository userPatientRelationshipRepository) {
        this.patientRepository = patientRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.userPatientRelationshipRepository = userPatientRelationshipRepository;
    }

    @Override
    @Transactional
    public PatientDto getPatientByPatientId(String patientId, Optional<String> userAuthId){
        //patientId is MRN, not Patient.id
        final Patient patient = patientRepository.findOneByMrn(patientId).orElseThrow(() -> new PatientNotFoundException("Patient Not Found!"));

        if(userAuthId.isPresent()){
            //Validate if the given userAuthId has access to the given MRN/PatientID
            final User user = userRepository.findOneByOauth2UserIdAndIsDisabled(userAuthId.get(), false)
                    .orElseThrow(() -> new UserNotFoundException("User Not Found!"));
            List<UserPatientRelationship> userPatientRelationshipList = userPatientRelationshipRepository.findAllByIdUserIdAndIdPatientId(user.getId(), patientId);

            if(userPatientRelationshipList == null || userPatientRelationshipList.size() < 1){
                throw new PatientNotFoundException("Patient Not Found!");
            }
        }
        return  modelMapper.map(patient, PatientDto.class);

    }


}
