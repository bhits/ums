package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.Address;
import gov.samhsa.c2s.ums.domain.AddressRepository;
import gov.samhsa.c2s.ums.domain.Demographics;
import gov.samhsa.c2s.ums.domain.DemographicsRepository;
import gov.samhsa.c2s.ums.domain.LocaleRepository;
import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.domain.PatientRepository;
import gov.samhsa.c2s.ums.domain.RoleRepository;
import gov.samhsa.c2s.ums.domain.Telecom;
import gov.samhsa.c2s.ums.domain.TelecomRepository;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserPatientRelationship;
import gov.samhsa.c2s.ums.domain.UserPatientRelationshipRepository;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCodeRepository;
import gov.samhsa.c2s.ums.domain.reference.CountryCodeRepository;
import gov.samhsa.c2s.ums.domain.reference.StateCodeRepository;
import gov.samhsa.c2s.ums.domain.valueobject.UserPatientRelationshipId;
import gov.samhsa.c2s.ums.infrastructure.ScimService;
import gov.samhsa.c2s.ums.service.dto.AccessDecisionDto;
import gov.samhsa.c2s.ums.service.dto.AddressDto;
import gov.samhsa.c2s.ums.service.dto.RelationDto;
import gov.samhsa.c2s.ums.service.dto.TelecomDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.PatientNotFoundException;
import gov.samhsa.c2s.ums.service.exception.UserActivationNotFoundException;
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
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
    private LocaleRepository localeRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private StateCodeRepository stateCodeRepository;
    @Autowired
    private CountryCodeRepository countryCodeRepository;

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

        /* Get User Entity from UserDto */
        User user = modelMapper.map(userDto, User.class);

        // Add user contact details to Telecom Table
        user.getDemographics().setTelecoms(modelMapper.map(userDto.getTelecoms(), new TypeToken<List<Telecom>>() {
        }.getType()));
        for (Telecom telecom : user.getDemographics().getTelecoms())
            telecom.setDemographics(user.getDemographics());

        user.getDemographics().setAddresses(modelMapper.map(userDto.getAddresses(), new TypeToken<List<Address>>() {
        }.getType()));
        for (Address address : user.getDemographics().getAddresses())
            address.setDemographics(user.getDemographics());

        user = userRepository.save(user);

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
        //Check if user account has been activated
        assertUserAccountHasBeenActivated(userId);
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
        //Check if user account has been activated
        assertUserAccountHasBeenActivated(userId);
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

        /* Get User Entity from UserDto */
        User user = userRepository.findOne(userId);

        user.setLocale(localeRepository.findByCode(userDto.getLocale()));
        user.setRoles(userDto.getRoles().stream().flatMap(roleDto -> roleRepository.findAllByCode(roleDto.getCode()).stream()).collect(Collectors.toSet()));
        user.getDemographics().setMiddleName(userDto.getMiddleName());
        user.getDemographics().setFirstName(userDto.getFirstName());
        user.getDemographics().setLastName(userDto.getLastName());
        user.getDemographics().setBirthDay(userDto.getBirthDate());
        user.getDemographics().setSocialSecurityNumber(userDto.getSocialSecurityNumber());
        user.getDemographics().setAdministrativeGenderCode(administrativeGenderCodeRepository.findByCode(userDto.getGenderCode()));
        if(userDto.getAddresses()!=null) {
            if(user.getDemographics().getAddresses()!=null) {
                mapAddressDtoToAddress(user.getDemographics().getAddresses().get(0),userDto.getAddresses().get(0));
            }
            else {
                Address address = mapAddressDtoToAddress(new Address(), userDto.getAddresses().get(0));
                address.setDemographics(user.getDemographics());
                user.getDemographics().setAddresses(Arrays.asList(address));
            }

        }

        if(userDto.getTelecoms()!=null) {
            if(user.getDemographics().getTelecoms()!=null) {
                mapTelecomDtoToTelcom(user.getDemographics().getTelecoms().get(0),userDto.getTelecoms().get(0));
            }
            else {
                Telecom telecom = mapTelecomDtoToTelcom(new Telecom(), userDto.getTelecoms().get(0));
                telecom.setDemographics(user.getDemographics());
                user.getDemographics().setTelecoms(Arrays.asList(telecom));
            }

        }

        user = userRepository.save(user);
    }

    private Address mapAddressDtoToAddress(Address address,AddressDto addressDto){
        address.setCity(addressDto.getCity());
        address.setStateCode(stateCodeRepository.findByCode(addressDto.getStateCode()));
        address.setCountryCode(countryCodeRepository.findByCode(addressDto.getCity()));
        address.setLine1(addressDto.getLine1());
        address.setLine2(addressDto.getLine2());
        if(addressDto.getUse()=="HOME")
            address.setUse(Address.Use.HOME);
        if(addressDto.getUse()=="WORK")
            address.setUse(Address.Use.WORK);
        return address;
    }

    private Telecom mapTelecomDtoToTelcom(Telecom telecom,TelecomDto telecomDto){
        telecom.setValue(telecomDto.getValue());

        if(telecomDto.getUse()=="HOME")
            telecom.setUse(Telecom.Use.HOME);
        if(telecomDto.getUse()=="WORK")
            telecom.setUse(Telecom.Use.WORK);

        if(telecomDto.getSystem()=="EMAIL")
            telecom.setSystem(Telecom.System.EMAIL);
        if(telecomDto.getUse()=="PHONE")
            telecom.setSystem(Telecom.System.PHONE);

        return telecom;
    }


    @Override
    public void updateUserLocale(Long userId, String localeCode) {

        /* Get User Entity from UserDto */
        User user = userRepository.findOne(userId);
        user.setLocale(localeRepository.findByCode(localeCode));
        user = userRepository.save(user);
    }

    @Override
    public void updateUserLocaleByUserAuthId(String userAuthId, String localeCode) {

        /* Get User Entity from UserDto */
        User user = userRepository.findOneByUserAuthIdAndDisabled(userAuthId,false).orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        user.setLocale(localeRepository.findByCode(localeCode));
        user = userRepository.save(user);
    }

    @Override
    public AccessDecisionDto accessDecision(String userAuthId, String patientMrn) {
        User user = userRepository.findOneByUserAuthIdAndDisabled(userAuthId,false).orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        Patient patient=patientRepository.findOneByMrn(patientMrn).orElseThrow(() -> new PatientNotFoundException("Patient Not Found!"));
        List<UserPatientRelationship> userPatientRelationshipList = userPatientRelationshipRepository.findAllByIdUserIdAndIdPatientId(user.getId(), patient.getId());

        if(userPatientRelationshipList == null || userPatientRelationshipList.size() < 1){
            return new AccessDecisionDto(false);
        }
        else
            return new AccessDecisionDto(true);
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
            return demographicsRepository.findAllByFirstNameLikesOrLastNameLikes("%" + firstName + "%", pageRequest)
                    .stream()
                    .map(demographics -> modelMapper.map(demographics.getUser(), UserDto.class))
                    .collect(Collectors.toList());
        } else if (token.countTokens() >= 2) {
            String firstName = token.nextToken(); // First Token is the first name
            String lastName = token.nextToken();  // Last Token is the last name
            return demographicsRepository.findAllByFirstNameLikesAndLastNameLikes("%" + firstName + "%", "%" + lastName + "%", pageRequest)
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

    private void assertUserAccountHasBeenActivated(Long userId) {
        Optional.of(userRepository.findOne(userId))
                .map(User::getUserAuthId)
                .filter(StringUtils::hasText)
                .orElseThrow(UserActivationNotFoundException::new);
    }
}
