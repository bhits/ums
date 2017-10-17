package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.Demographics;
import gov.samhsa.c2s.ums.domain.DemographicsRepository;
import gov.samhsa.c2s.ums.domain.Identifier;
import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserPatientRelationship;
import gov.samhsa.c2s.ums.domain.UserPatientRelationshipRepository;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.service.dto.IdentifierSystemDto;
import gov.samhsa.c2s.ums.service.dto.PatientDto;
import gov.samhsa.c2s.ums.service.exception.PatientNotFoundException;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PatientServiceImpl implements PatientService {

    @Autowired
    private DemographicsRepository demographicsRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserPatientRelationshipRepository userPatientRelationshipRepository;
    @Autowired
    private UmsProperties umsProperties;

    @Override
    @Transactional(readOnly = true)
    public PatientDto getPatientByPatientId(String patientId, Optional<String> userAuthId) {
        //patientId is MRN, not Patient.id
        final Patient patient = demographicsRepository.findOneByIdentifiersValueAndIdentifiersIdentifierSystemSystem(patientId, umsProperties.getMrn().getCodeSystem())
                .map(Demographics::getPatient)
                .orElseThrow(() -> new PatientNotFoundException("Patient Not Found!"));

        if (userAuthId.isPresent()) {
            //Validate if the given userAuthId has access to the given MRN/PatientID
            final User user = userRepository.findByUserAuthIdAndDisabled(userAuthId.get(), false)
                    .orElseThrow(() -> new UserNotFoundException("User Not Found!"));
            List<UserPatientRelationship> userPatientRelationshipList = userPatientRelationshipRepository.findAllByIdUserIdAndIdPatientId(user.getId(), patient.getId());

            if (userPatientRelationshipList == null || userPatientRelationshipList.size() < 1) {
                throw new PatientNotFoundException("Patient Not Found!");
            }
        }
        return modelMapper.map(patient, PatientDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDto getPatientByIdentifierValueAndIdentifierSystem(String identifierValue, String identifierSystem) {
        final String mrnIdentifierSystem = umsProperties.getMrn().getCodeSystem();
        final Patient patient = demographicsRepository.findOneByIdentifiersValueAndIdentifiersIdentifierSystemSystem(identifierValue, identifierSystem)
                .filter(demographics -> {
                    final boolean identifierSystemMatchesMrnSystem = mrnIdentifierSystem.equalsIgnoreCase(identifierSystem);
                    if (!identifierSystemMatchesMrnSystem)
                        log.debug("Identifier system must match MRN system for patients: " + mrnIdentifierSystem);
                    return identifierSystemMatchesMrnSystem;
                })
                .map(Demographics::getPatient)
                .orElseThrow(() -> new PatientNotFoundException("Patient Not Found!"));
        return modelMapper.map(patient, PatientDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDto> getPatientByUserAuthId(String userAuthId) {
        User user = userRepository.findByUserAuthIdAndDisabled(userAuthId, false).orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        List<UserPatientRelationship> userPatientRelationshipList = userPatientRelationshipRepository.findAllByIdUserId(user.getId());
        List<PatientDto> patientDtos = new ArrayList<>();
        userPatientRelationshipList.stream().forEach(userPatientRelationship -> {
            PatientDto patientDto = modelMapper.map(userPatientRelationship.getId().getPatient(), PatientDto.class);
            patientDto.setRelationship(userPatientRelationship.getId().getRelationship().getId().getRole().getCode());
            patientDtos.add(patientDto);
        });
        return patientDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public IdentifierSystemDto getPatientMrnIdentifierSystemByPatientId(String patientId) {
        //patientId is MRN, not Patient.id
        final Patient patient = demographicsRepository.findOneByIdentifiersValueAndIdentifiersIdentifierSystemSystem(patientId, umsProperties.getMrn().getCodeSystem())
                .map(Demographics::getPatient)
                .orElseThrow(() -> new PatientNotFoundException("Patient Not Found!"));

        Identifier patientMrnIdentifier = patient.getDemographics().getIdentifiers().stream().filter(i -> i.getValue().equalsIgnoreCase(patientId)).findFirst().orElseThrow(() -> new PatientNotFoundException("Patient Not Found!"));
        return modelMapper.map(patientMrnIdentifier.getIdentifierSystem(), IdentifierSystemDto.class);
    }
}
