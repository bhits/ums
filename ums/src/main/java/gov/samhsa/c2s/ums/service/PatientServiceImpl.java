package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.domain.PatientRepository;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.service.dto.PatientDto;
import gov.samhsa.c2s.ums.service.exception.PatientNotFoundException;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import org.modelmapper.ModelMapper;

import javax.transaction.Transactional;

public class PatientServiceImpl implements PatientService{

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public PatientServiceImpl(PatientRepository patientRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public PatientDto getPatientByOauth2UserId(String oAuth2UserId){
        final User user = userRepository.findOneByOauth2UserIdAndIsDisabled(oAuth2UserId, false)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        final Patient patient = patientRepository.findOneByUserId(user.getId()).orElseThrow(() -> new PatientNotFoundException("Patient Not Found!"));
        return modelMapper.map(patient, PatientDto.class);
    }


}
