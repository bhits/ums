package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.Address;
import gov.samhsa.c2s.ums.domain.AddressRepository;
import gov.samhsa.c2s.ums.domain.Demographics;
import gov.samhsa.c2s.ums.domain.DemographicsRepository;
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
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final Integer PAGE_NUMBER = 0;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdministrativeGenderCodeRepository administrativeGenderCodeRepository;
    @Autowired
    private UmsProperties umsProperties;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private MrnService mrnService;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private TelecomRepository telecomRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserPatientRelationshipRepository userPatientRelationshipRepository;
    @Autowired
    private ScimService scimService;
    @Autowired
    private DemographicsRepository demographicsRepository;


    @Override
    @Transactional
    public void registerUser(UserDto userDto) {

        // Step 1: Create User Record and User Role Mapping in UMS

        // Add UserAddress to Address Table
       //userDto.getAddresses().stream().map(addressDto -> modelMapper.map(addressDto, Address.class)).forEach(address -> addressRepository.save(address));

        // Add user contact details to Telecom Table



        /* Get User Entity from UserDto */
        User user = modelMapper.map(userDto, User.class);
        // set Address Id to User Entity
       // user.getDemographics().getAddresses().add(address);

        /* Add record to User and User_Roles table */


        // Add user contact details to Telecom Table
        user.getDemographics().setTelecoms(modelMapper.map(userDto.getTelecoms(), new TypeToken<List<Telecom>>() {
        }.getType()));
        for (Telecom telecom : user.getDemographics().getTelecoms())
            telecom.setDemographics(user.getDemographics());
        //telecomRepository.save(user.getDemographics().getTelecoms());

        user.getDemographics().setAddresses(modelMapper.map(userDto.getAddresses(), new TypeToken<List<Address>>() {
        }.getType()));
        for (Address address : user.getDemographics().getAddresses())
            address.setDemographics(user.getDemographics());

        user = userRepository.save(user);

       // telecomRepository.save(user.getDemographics().getTelecoms());

        /*
        Step 2: Create User Patient Record in UMS  if User is a Patient
        Add User Patient Record if the role is patient
        TODO remove the hardcoding with FHIR enum value
        */
        if (userDto.getRoles().stream().anyMatch(roleDto -> roleDto.getCode().equalsIgnoreCase("patient"))) {
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
        patient.setDemographics(user.getDemographics());
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
        User user = userRepository.findOneByIdAndDisabled(userId, false)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        user.setDisabled(true);
        //
        /**
         * Use OAuth API to set users.active to false.
         * Doing so will not let a user to login.
         * Also known as "Soft Delete".
         */
        scimService.setUserAsInactive(user.getUserAuthId());
        User save = userRepository.save(user);
    }

    @Override
    public void enableUser(Long userId) {

        //Set isDisabled to false in the User table
        User user = userRepository.findOneByIdAndDisabled(userId, true)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        user.setDisabled(false);

        /**
         * Use OAuth API to set users.active to true.

         */
        scimService.setUserAsActive(user.getUserAuthId());
        User save = userRepository.save(user);
    }

    @Override
    public void updateUser(Long userId, UserDto userDto) {
    }


    @Override
    public UserDto getUser(Long userId) {
        final User user = userRepository.findOne(userId);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserByUserAuthId(String userAuthId) {
        final User user = userRepository.findOneByUserAuthIdAndDisabled(userAuthId, false)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public Page<UserDto> getAllUsers(Optional<Integer> page, Optional<Integer> size) {
        final PageRequest pageRequest = new PageRequest(page.filter(p -> p >= 0).orElse(0),
                size.filter(s -> s > 0 && s <= umsProperties.getPagination().getMaxSize())
                        .orElse(umsProperties.getPagination().getDefaultSize()));
        final Page<User> usersPage = userRepository.findAll(pageRequest);
        final List<User> userList = usersPage.getContent();
        final List<UserDto> getUserDtoList = userListToUserDtoList(userList);
        return new PageImpl<>(getUserDtoList, pageRequest, usersPage.getTotalElements());
    }

    public List<UserDto> searchUsersByFirstNameAndORLastName(StringTokenizer token) {
        Pageable pageRequest = new PageRequest(PAGE_NUMBER, umsProperties.getPagination().getDefaultSize());
        if (token.countTokens() == 1) {
            String firstName = token.nextToken(); // First Token could be first name or the last name
            return demographicsRepository.findAllByFirstNameLikesOrLastNameLikes("%" + firstName + "%",  pageRequest)
                    .stream()
                    .map(demographics -> modelMapper.map(demographics.getUser(), UserDto.class))
                    .collect(Collectors.toList());
        } else if (token.countTokens() >= 2) {
            String firstName = token.nextToken(); // First Token is the first name
            String lastName = token.nextToken();  // Last Token is the last name
            return demographicsRepository.findAllByFirstNameLikesAndLastNameLikes("%" + firstName + "%", "%" + lastName + "%",  pageRequest)
                    .stream()
                    .map(demographics -> modelMapper.map(demographics.getUser(), UserDto.class))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<UserDto> searchUsersByDemographic(String firstName,
                                                  String lastName,
                                                  LocalDate birthDate,
                                                  String genderCode) {
        List<Demographics> demographicsesList;
        final AdministrativeGenderCode administrativeGenderCode = administrativeGenderCodeRepository.findByCode(genderCode);
        demographicsesList = demographicsRepository.findAllByFirstNameAndLastNameAndBirthDayAndAdministrativeGenderCode(firstName, lastName,
                birthDate, administrativeGenderCode);
        if (demographicsesList.size() < 1) {
            throw new UserNotFoundException("User Not Found!");
        } else {
            return demographicsesListToUserDtoList(demographicsesList);
        }
    }

    private List<UserDto> demographicsesListToUserDtoList(List<Demographics> demographicsesList) {
        List<UserDto> getUserDtoList = new ArrayList<>();

        if (demographicsesList != null && demographicsesList.size() > 0) {
            for (Demographics temp : demographicsesList) {
                getUserDtoList.add(modelMapper.map(temp.getUser(), UserDto.class));
            }
        }
        return getUserDtoList;
    }

    private List<UserDto> userListToUserDtoList(List<User> userList) {
        List<UserDto> getUserDtoList = new ArrayList<>();

        if (userList != null && userList.size() > 0) {
            for (User temp : userList) {
                getUserDtoList.add(modelMapper.map(temp, UserDto.class));
            }
        }
        return getUserDtoList;
    }



}
