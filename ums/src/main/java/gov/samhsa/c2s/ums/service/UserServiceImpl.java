package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.Address;
import gov.samhsa.c2s.ums.domain.AddressRepository;
import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.domain.PatientRepository;
import gov.samhsa.c2s.ums.domain.Telecom;
import gov.samhsa.c2s.ums.domain.TelecomRepository;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserPatientRelationship;
import gov.samhsa.c2s.ums.domain.UserPatientRelationshipRepository;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCodeRepository;
import gov.samhsa.c2s.ums.domain.valueobject.UserPatientRelationshipId;
import gov.samhsa.c2s.ums.infrastructure.ScimService;
import gov.samhsa.c2s.ums.service.dto.GetUserResponseDto;
import gov.samhsa.c2s.ums.service.dto.RelationDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AdministrativeGenderCodeRepository administrativeGenderCodeRepository;
    private final UmsProperties umsProperties;
    private final ModelMapper modelMapper;
    private final MrnService mrnService;
    private final PatientRepository patientRepository;
    private final TelecomRepository telecomRepository;
    private final AddressRepository addressRepository;
    private final UserPatientRelationshipRepository userPatientRelationshipRepository;
    private final ScimService scimService;


    @Autowired
    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository, AdministrativeGenderCodeRepository administrativeGenderCodeRepository, UmsProperties umsProperties, MrnService mrnService, PatientRepository patientRepository, TelecomRepository telecomRepository, AddressRepository addressRepository, UserPatientRelationshipRepository userPatientRelationshipRepository,ScimService scimService) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.administrativeGenderCodeRepository = administrativeGenderCodeRepository;
        this.umsProperties = umsProperties;
        this.mrnService = mrnService;
        this.patientRepository = patientRepository;
        this.telecomRepository = telecomRepository;
        this.addressRepository = addressRepository;
        this.userPatientRelationshipRepository = userPatientRelationshipRepository;
        this.scimService = scimService;
    }

    @Override
    @Transactional
    public void registerUser(UserDto userDto) {

        // Step 1: Create User Record and User Role Mapping in UMS

        // Add UserAddress to Address Table
        Address address = addressRepository.save(modelMapper.map(userDto.getAddress(), Address.class));

        /* Get User Entity from UserDto */
        User user = modelMapper.map(userDto, User.class);
        // set Address Id to User Entity
        user.setAddress(address);

        /* Add record to User and User_Roles table */
        user = userRepository.save(user);

        // Add user contact details to Telecom Table
        user.setTelecoms(modelMapper.map(userDto.getTelecom(), new TypeToken<List<Telecom>>() {
        }.getType()));
        for (Telecom telecom : user.getTelecoms())
            telecom.setUser(user);
        telecomRepository.save(user.getTelecoms());

        /*
        Step 2: Create User Patient Record in UMS  if User is a Patient
        Add User Patient Record if the role is patient
        TODO remove the hardcoding with FHIR enum value
        */
        if (userDto.getRole().stream().anyMatch(roleDto -> roleDto.getCode().equalsIgnoreCase("patient")))
        {
            Patient patient = createPatient(user);
            // Step 2.1: Create User Patient Relationship Mapping in UMS
            // Add User patient relationship if User is a Patient
            createUserPatientRelationship(user.getId(), patient.getId(), "patient");
            // Publish FHIR Patient to FHir Service

        }

    }


    private Patient createPatient(User user) {
        //set the patient object
        Patient patient = new Patient();
        patient.setMrn(mrnService.generateMrn());
        patient.setUser(user);
        return patientRepository.save(patient);
    }

    private void createUserPatientRelationship(long userId, long patientId, String role) {
        RelationDto relationDto = new RelationDto(userId, patientId, role);
        UserPatientRelationship userPatientRelationship = new UserPatientRelationship();
        userPatientRelationship.setId(modelMapper.map(relationDto, UserPatientRelationshipId.class));
        userPatientRelationshipRepository.save(userPatientRelationship);
    }


    @Override
    @Transactional
    public void disableUser(Long userId) {

        //Set isDisabled to true in the User table
        User user = userRepository.findOneByIdAndIsDisabled(userId, false)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        user.setDisabled(true);
        //
        /**
         * Use OAuth API to set users.active to false.
         * Doing so will not let a user to login.
         * Also known as "Soft Delete".
         */
        scimService.setUserAsInactive(user.getOauth2UserId());
        User save = userRepository.save(user);
    }

    @Override
    public void enableUser(Long userId) {

        //Set isDisabled to false in the User table
        User user = userRepository.findOneByIdAndIsDisabled(userId, true)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        user.setDisabled(false);

        /**
         * Use OAuth API to set users.active to true.

         */
        scimService.setUserAsActive(user.getOauth2UserId());
        User save = userRepository.save(user);
    }

    @Override
    public void updateUser(Long userId, UserDto userDto) {
    }


    @Override
    public Object getUser(Long userId) {
        final User user = userRepository.findOneByIdAndIsDisabled(userId, false)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        return modelMapper.map(user, GetUserResponseDto.class);
    }

    @Override
    public Object getUserByOAuth2Id(String oAuth2UserId) {
        final User user = userRepository.findOneByOauth2UserIdAndIsDisabled(oAuth2UserId, false)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        return modelMapper.map(user, GetUserResponseDto.class);
    }

    @Override
    public Page<GetUserResponseDto> getAllUsers(Optional<Integer> page, Optional<Integer> size) {
        final PageRequest pageRequest = new PageRequest(page.filter(p -> p >= 0).orElse(0),
                size.filter(s -> s > 0 && s <= umsProperties.getPagination().getMaxSize())
                        .orElse(umsProperties.getPagination().getDefaultSize()));
        final Page<User> usersPage = userRepository.findAllByIsDisabled(false, pageRequest);
        final List<User> userList = usersPage.getContent();
        final List<GetUserResponseDto> getUserDtoList = userListToGetUserDtoList(userList);
        return new PageImpl<>(getUserDtoList, pageRequest, usersPage.getTotalElements());
    }

    @Override
    public List<GetUserResponseDto> searchUsersByFirstNameAndORLastName(StringTokenizer token) {

        List<User> userList;
        Integer pageNumber = 0;
        Pageable pageRequest = new PageRequest(pageNumber, umsProperties.getPagination().getDefaultSize());

        if (token.countTokens() == 1) {
            String firstName = token.nextToken(); // First Token could be first name or the last name
            userList = userRepository.findAllByFirstNameLikesOrLastNameLikesAndIsDisabled("%" + firstName + "%", false, pageRequest);
        } else if (token.countTokens() >= 2) {
            String firstName = token.nextToken(); // First Token is the first name
            String lastName = token.nextToken();  // Last Token is the last name
            userList = userRepository.findAllByFirstNameLikesAndLastNameLikesAndIsDisabled("%" + firstName + "%", "%" + lastName + "%", false, pageRequest);
        } else {
            userList = new ArrayList<>();
        }
        if (userList.size() < 1) {
            throw new UserNotFoundException("User Not Found!");
        } else {
            return userListToGetUserDtoList(userList);
        }
    }

    @Override
    public List<GetUserResponseDto> searchUsersByDemographic(String firstName,
                                                  String lastName,
                                                  LocalDate birthDate,
                                                  String genderCode) {
        List<User> userList;
        final AdministrativeGenderCode administrativeGenderCode = administrativeGenderCodeRepository.findByCode(genderCode);
        userList = userRepository.findAllByFirstNameAndLastNameAndBirthDayAndAdministrativeGenderCodeAndIsDisabled(firstName, lastName,
                birthDate, administrativeGenderCode, false);
        if (userList.size() < 1) {
            throw new UserNotFoundException("User Not Found!");
        } else {
            return userListToGetUserDtoList(userList);
        }
    }

    private List<GetUserResponseDto> userListToGetUserDtoList(List<User> userList) {
        List<GetUserResponseDto> getUserDtoList = new ArrayList<>();

        if (userList != null && userList.size() > 0) {
            for (User tempUser : userList) {
                getUserDtoList.add(modelMapper.map(tempUser, GetUserResponseDto.class));
            }
        }
        return getUserDtoList;
    }

}
