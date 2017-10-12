package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.Address;
import gov.samhsa.c2s.ums.domain.Demographics;
import gov.samhsa.c2s.ums.domain.DemographicsRepository;
import gov.samhsa.c2s.ums.domain.Identifier;
import gov.samhsa.c2s.ums.domain.IdentifierRepository;
import gov.samhsa.c2s.ums.domain.IdentifierSystem;
import gov.samhsa.c2s.ums.domain.IdentifierSystemRepository;
import gov.samhsa.c2s.ums.domain.LocaleRepository;
import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.domain.PatientRepository;
import gov.samhsa.c2s.ums.domain.Role;
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
import gov.samhsa.c2s.ums.infrastructure.FisClient;
import gov.samhsa.c2s.ums.infrastructure.ScimService;
import gov.samhsa.c2s.ums.service.dto.AccessDecisionDto;
import gov.samhsa.c2s.ums.service.dto.AddressDto;
import gov.samhsa.c2s.ums.service.dto.IdentifierDto;
import gov.samhsa.c2s.ums.service.dto.RelationDto;
import gov.samhsa.c2s.ums.service.dto.TelecomDto;
import gov.samhsa.c2s.ums.service.dto.UpdateUserLimitedFieldsDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import gov.samhsa.c2s.ums.service.exception.InvalidIdentifierSystemException;
import gov.samhsa.c2s.ums.service.exception.MissingEmailException;
import gov.samhsa.c2s.ums.service.exception.PatientNotFoundException;
import gov.samhsa.c2s.ums.service.exception.UnassignableIdentifierException;
import gov.samhsa.c2s.ums.service.exception.UserActivationNotFoundException;
import gov.samhsa.c2s.ums.service.exception.UserNotFoundException;
import gov.samhsa.c2s.ums.service.mapping.PatientToMrnConverter;
import gov.samhsa.c2s.ums.service.mapping.UserToMrnConverter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;


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
    private RoleRepository roleRepository;
    @Autowired
    private StateCodeRepository stateCodeRepository;
    @Autowired
    private CountryCodeRepository countryCodeRepository;
    @Autowired
    private UserPatientRelationshipRepository userPatientRelationshipRepository;
    @Autowired
    private DemographicsRepository demographicsRepository;
    @Autowired
    private IdentifierSystemRepository identifierSystemRepository;
    @Autowired
    private TelecomRepository telecomRepository;
    @Autowired
    private IdentifierRepository identifierRepository;
    @Autowired
    private UserToMrnConverter userToMrnConverter;
    @Autowired
    private PatientToMrnConverter patientToMrnConverter;
    @Autowired
    private LocaleRepository localeRepository;
    @Autowired
    private ScimService scimService;

    @Autowired
    private FisClient fisClient;

    @Override
    @Transactional
    public UserDto registerUser(UserDto userDto) {

        // Step 1: Create User Record and User Role Mapping in UMS

        /* Get User Entity from UserDto */
        final User user = modelMapper.map(userDto, User.class);

        // Identifiers
        final Set<UmsProperties.RequiredIdentifierSystem> allRequiredIdentifierSystems = getAllRequiredIdentifierSystems(user);
        final Set<UmsProperties.RequiredIdentifierSystem> systemGeneratedIdentifierSystems = getRequiredAndSystemGeneratedIdentifierSystems(allRequiredIdentifierSystems);
        final List<IdentifierDto> consolidatedIdentifierDtos = getConsolidatedIdentifierDtos(userDto, systemGeneratedIdentifierSystems);
        // Find or create the identifiers and save them to identifierRepository
        final Stream<Identifier> nonSystemGeneratedIdentifiers = consolidatedIdentifierDtos.stream()
                .map(idDto -> identifierRepository
                        .findByValueAndIdentifierSystemSystem(idDto.getValue(), idDto.getSystem())
                        .orElseGet(() -> createIdentifier(idDto, systemGeneratedIdentifierSystems)));
        final Stream<Identifier> systemGeneratedIdentifiers = systemGeneratedIdentifierSystems.stream()
                .map(requiredIdentifierSystem -> {
                    final IdentifierSystem identifierSystem = identifierSystemRepository.findBySystem(requiredIdentifierSystem.getSystem()).orElseThrow(InvalidIdentifierSystemException::new);
                    final String identifierValue = generateIdentifier(requiredIdentifierSystem.getAlgorithm());
                    return Identifier.of(identifierValue, identifierSystem);
                });
        final List<Identifier> identifiers = Stream.concat(systemGeneratedIdentifiers, nonSystemGeneratedIdentifiers).collect(toList());
        assertAllRequiredIdentifiersExist(identifiers, allRequiredIdentifierSystems);
        identifierRepository.save(identifiers);
        // Assign these identifiers to the user
        user.getDemographics().setIdentifiers(identifiers);

        // Add user contact details to Telecom Table
        user.getDemographics().setTelecoms(modelMapper.map(userDto.getTelecoms(), new TypeToken<List<Telecom>>() {
        }.getType()));
        for (Telecom telecom : user.getDemographics().getTelecoms())
            telecom.setDemographics(user.getDemographics());

        user.getDemographics().setAddresses(modelMapper.map(userDto.getAddresses(), new TypeToken<List<Address>>() {
        }.getType()));
        for (Address address : user.getDemographics().getAddresses())
            address.setDemographics(user.getDemographics());

        userRepository.save(user);

        /*
        Step 2: Create User Patient Record in UMS  if User is a Patient
        Add User Patient Record if the role is patient
        TODO remove the hardcoding with FHIR enum value
        */
        if (userDto.getRoles().stream().anyMatch(roleDto -> roleDto.getCode().equalsIgnoreCase("patient"))) {
            // Assert that the patient has at least one email OR a registrationPurposeEmail
            final boolean patientHasEmail = user.getDemographics().getTelecoms().stream().map(Telecom::getSystem).anyMatch(Telecom.System.EMAIL::equals);
            if (!patientHasEmail && !userDto.getRegistrationPurposeEmail().filter(StringUtils::hasText).isPresent()) {
                throw new MissingEmailException("At least one of personal email OR a registration purpose email is required");
            }

            Patient patient = createPatient(user, userDto.getRegistrationPurposeEmail());
            // Step 2.1: Create User Patient Relationship Mapping in UMS
            // Add User patient relationship if User is a Patient
            createUserPatientRelationship(user.getId(), patient.getId(), "patient");
            // Publish FHIR Patient to FHir Service
            if (umsProperties.getFhir().getPublish().isEnabled()) {
                userDto.setMrn(patientToMrnConverter.convert(patient));
                fisClient.publishFhirPatient(modelMapper.map(user, UserDto.class));
            }
        }

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public void disableUser(Long userId, Optional<String> lastUpdatedBy) {
        //Check if user account has been activated
        assertUserAccountHasBeenActivated(userId);
        //Set isDisabled to true in the User table
        User user = userRepository.findByIdAndDisabled(userId, false)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        user.setDisabled(true);
        user.setLastUpdatedBy(lastUpdatedBy.orElse(null));
        //
        /**
         * Use OAuth API to set users.active to false.
         * Doing so will not let a user to login.
         * Also known as "Soft Delete".
         */
        scimService.inactivateUser(user.getUserAuthId());
        User save = userRepository.save(user);
    }

    @Override
    @Transactional
    public void enableUser(Long userId, Optional<String> lastUpdatedBy) {
        //Check if user account has been activated
        assertUserAccountHasBeenActivated(userId);
        //Set isDisabled to false in the User table
        User user = userRepository.findByIdAndDisabled(userId, true)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        user.setDisabled(false);
        user.setLastUpdatedBy(lastUpdatedBy.orElse(null));

        /**
         * Use OAuth API to set users.active to true.

         */
        scimService.activateUser(user.getUserAuthId());
        User save = userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {

        /* Get User Entity from UserDto */

        final User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.setLastUpdatedBy(userDto.getLastUpdatedBy());
        user.setLocale(localeRepository.findByCode(userDto.getLocale()));
        user.setRoles(userDto.getRoles().stream().flatMap(roleDto -> roleRepository.findAllByCode(roleDto.getCode()).stream()).collect(toSet()));
        user.getDemographics().setMiddleName(userDto.getMiddleName());
        user.getDemographics().setFirstName(userDto.getFirstName());
        user.getDemographics().setLastName(userDto.getLastName());
        user.getDemographics().setBirthDay(userDto.getBirthDate());

        // Update registration purpose email
        final String registrationPurposeEmail = userDto.getRegistrationPurposeEmail().filter(StringUtils::hasText).map(String::trim).orElse(null);
        user.getDemographics().getPatient().setRegistrationPurposeEmail(registrationPurposeEmail);

        // Identifiers
        // Find system generated identifier systems based on the user roles
        final Set<UmsProperties.RequiredIdentifierSystem> allRequiredIdentifierSystems = getAllRequiredIdentifierSystems(user);
        final Set<UmsProperties.RequiredIdentifierSystem> systemGeneratedIdentifierSystems = getRequiredAndSystemGeneratedIdentifierSystems(allRequiredIdentifierSystems);
        final List<IdentifierDto> consolidatedIdentifierDtos = getConsolidatedIdentifierDtos(userDto, systemGeneratedIdentifierSystems);
        // Find the non-system-generated identifiers that have different values in the request to remove them
        final List<Identifier> identifiersToRemove = Optional.of(user)
                .map(User::getDemographics)
                .map(Demographics::getIdentifiers)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(id -> systemGeneratedIdentifierSystems.stream()
                        .map(UmsProperties.RequiredIdentifierSystem::getSystem)
                        .noneMatch(id.getIdentifierSystem().getSystem()::equals))
                .filter(id -> consolidatedIdentifierDtos.stream()
                        .noneMatch(idDto -> deepEquals(id, idDto)))
                .collect(toList());
        // Remove the different non-system-generated identifiers from the user
        Optional.of(user)
                .map(User::getDemographics)
                .map(Demographics::getIdentifiers)
                .ifPresent(identifiers -> identifiers.removeAll(identifiersToRemove));
        // Find the different and non-system-generated identifiers from the request
        final List<Identifier> identifiersToAdd = consolidatedIdentifierDtos.stream()
                .filter(idDto -> Optional.of(user)
                        .map(User::getDemographics)
                        .map(Demographics::getIdentifiers)
                        .orElseGet(Collections::emptyList).stream()
                        .noneMatch(id -> deepEquals(id, idDto)))
                .filter(idDto -> systemGeneratedIdentifierSystems.stream()
                        .map(UmsProperties.RequiredIdentifierSystem::getSystem)
                        .noneMatch(system -> system.equals(idDto.getSystem())))
                .map(idDto -> identifierRepository.findByValueAndIdentifierSystemSystem(idDto.getValue(), idDto.getSystem())
                        .orElseGet(() -> createIdentifier(idDto, systemGeneratedIdentifierSystems)))
                .collect(toList());
        // Save the different and non-system-generated identifiers and add them to the user
        identifierRepository.save(identifiersToAdd);
        // Assert the new identifiers to add does not contain unassignable identifiers
        assertDoesNotContainUnassignableIdentifiers(user, identifiersToAdd);
        // Add new identifiers
        user.getDemographics().getIdentifiers().addAll(identifiersToAdd);
        assertAllRequiredIdentifiersExist(user.getDemographics().getIdentifiers(), allRequiredIdentifierSystems);

        user.getDemographics().setAdministrativeGenderCode(administrativeGenderCodeRepository.findByCode(userDto.getGenderCode()));

        //update address
        List<Address> addresses = user.getDemographics().getAddresses();
        if (userDto.getAddresses() != null) {
            userDto.getAddresses().stream().forEach(addressDto -> {
                Optional<Address> tempAddress = addresses.stream().filter(address -> address.getUse().toString().equals(addressDto.getUse())).findFirst();
                if (tempAddress.isPresent()) {
                    mapAddressDtoToAddress(tempAddress.get(), addressDto);
                } else {
                    Address address = mapAddressDtoToAddress(new Address(), addressDto);
                    address.setDemographics(user.getDemographics());
                    addresses.add(address);
                }
            });
        }

        //update telephone
        final List<Telecom> telecoms = user.getDemographics().getTelecoms();
        final List<TelecomDto> telecomDtos = Optional.ofNullable(userDto.getTelecoms()).orElseGet(Collections::emptyList);

        // Assert emails
        Optional<Patient> patientOptional = Optional.of(user)
                .map(User::getDemographics)
                .map(Demographics::getPatient);
        assertEmails(userDto, patientOptional.isPresent());

        // Find telecoms to remove
        final List<Telecom> telecomsToRemove = telecoms.stream()
                .filter(telecom -> telecomDtos.stream()
                        .noneMatch(telecomDto -> deepEquals(telecom, telecomDto)))
                .collect(toList());

        telecomRepository.deleteInBatch(telecomsToRemove);

        // Find telecoms to add
        final List<Telecom> telecomsToAdd = telecomDtos.stream()
                .filter(telecomDto -> telecoms.stream()
                        .noneMatch(telecom -> deepEquals(telecom, telecomDto)))
                .map(telecomDto -> mapTelecomDtoToTelcom(new Telecom(), telecomDto))
                .collect(toList());

        telecomsToAdd.stream().forEach(telecom -> telecom.setDemographics(user.getDemographics()));
        telecomRepository.save(telecomsToAdd);
        user.getDemographics().getTelecoms().addAll(telecomsToAdd);

        patientOptional
                .ifPresent(patient -> {
                    userDto.getRegistrationPurposeEmail().ifPresent(patient::setRegistrationPurposeEmail);
                    patientRepository.save(patient);
                    if (umsProperties.getFhir().getPublish().isEnabled()) {
                        userDto.setMrn(userToMrnConverter.convert(user));
                        fisClient.updateFhirPatient(modelMapper.map(user, UserDto.class));
                    }
                });

        final User updatedUser = userRepository.save(user);

        // If system account exists, also update basic user info in authorization server
        Optional.of(user)
                .map(User::getUserAuthId)
                .ifPresent(userAuthId -> scimService.updateUserBasicInfo(userAuthId, userDto));
        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    public UserDto updateUserLimitedFields(Long userId, UpdateUserLimitedFieldsDto updateUserLimitedFieldsDto) {
        // Get user from database as UserDto object
        User user = userRepository.findOne(userId);

        // Update address
        List<Address> addresses = user.getDemographics().getAddresses();
        AddressDto newHomeAddressDto = new AddressDto(updateUserLimitedFieldsDto.getHomeAddress(), UseTypes.HOME.toString());

        Optional<Address> oldHomeAddress = addresses.parallelStream()
                .filter(address -> address.getUse().equals(Address.Use.HOME))
                .findFirst();

        if (oldHomeAddress.isPresent()) {
            mapAddressDtoToAddress(oldHomeAddress.get(), newHomeAddressDto);
        } else {
            Address address = mapAddressDtoToAddress(new Address(), newHomeAddressDto);
            address.setDemographics(user.getDemographics());
            addresses.add(address);
        }

        List<Telecom> userTelecoms = user.getDemographics().getTelecoms();

        // Update home phone telecom
        Optional<String> newHomePhoneOpt = Optional.ofNullable(updateUserLimitedFieldsDto.getHomePhone());
        Optional<TelecomDto> newHomePhoneTelecomDto = newHomePhoneOpt.map(s -> new TelecomDto(SystemTypes.PHONE.toString(), s, UseTypes.HOME.toString()));

        Optional<Telecom> oldHomePhoneTelecom = userTelecoms.parallelStream()
                .filter(telecom ->
                        telecom.getSystem().equals(Telecom.System.PHONE)
                                && telecom.getUse().equals(Telecom.Use.HOME))
                .findFirst();

        if (newHomePhoneTelecomDto.isPresent()) {
            if (oldHomePhoneTelecom.isPresent()) {
                oldHomePhoneTelecom.get().setValue(newHomePhoneTelecomDto.get().getValue());
            } else {
                Telecom telecomToAdd = mapTelecomDtoToTelcom(new Telecom(), newHomePhoneTelecomDto.get());
                telecomToAdd.setDemographics(user.getDemographics());
                userTelecoms.add(telecomToAdd);
            }
        } else {
            if (oldHomePhoneTelecom.isPresent()) {
                // Remove home phone telecom if it already exists
                oldHomePhoneTelecom.get().setDemographics(null);
                userTelecoms.remove(oldHomePhoneTelecom.get());
            }
        }

        // Update home email telecom
        Optional<String> newHomeEmailOpt = Optional.ofNullable(updateUserLimitedFieldsDto.getHomeEmail());
        Optional<TelecomDto> newHomeEmailTelecomDto = newHomeEmailOpt.map(s -> new TelecomDto(SystemTypes.EMAIL.toString(), s, UseTypes.HOME.toString()));

        Optional<Telecom> oldHomeEmailTelecom = userTelecoms.parallelStream()
                .filter(telecom ->
                        telecom.getSystem().equals(Telecom.System.EMAIL)
                                && telecom.getUse().equals(Telecom.Use.HOME))
                .findFirst();

        if (newHomeEmailTelecomDto.isPresent()) {
            if (oldHomeEmailTelecom.isPresent()) {
                oldHomeEmailTelecom.get().setValue(newHomeEmailTelecomDto.get().getValue());
            } else {
                Telecom telecomToAdd = mapTelecomDtoToTelcom(new Telecom(), newHomeEmailTelecomDto.get());
                telecomToAdd.setDemographics(user.getDemographics());
                userTelecoms.add(telecomToAdd);
            }
        } else {
            if (oldHomeEmailTelecom.isPresent()) {
                // Remove home email telecom if it already exists
                oldHomeEmailTelecom.get().setDemographics(null);
                userTelecoms.remove(oldHomeEmailTelecom.get());
            }
        }

        //Update last updated by
        user.setLastUpdatedBy(updateUserLimitedFieldsDto.getLastUpdatedBy());

        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    @Transactional
    public void updateUserLocale(Long userId, String localeCode) {

        /* Get User Entity from UserDto */
        User user = userRepository.findOne(userId);
        user.setLocale(localeRepository.findByCode(localeCode));
        user = userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserLocaleByUserAuthId(String userAuthId, String localeCode) {

        /* Get User Entity from UserDto */
        User user = userRepository.findByUserAuthIdAndDisabled(userAuthId, false).orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        user.setLocale(localeRepository.findByCode(localeCode));
        user = userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AccessDecisionDto accessDecision(String userAuthId, String patientMrn) {
        final User user = userRepository.findByUserAuthIdAndDisabled(userAuthId, false).orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        final Patient patient = demographicsRepository.findOneByIdentifiersValueAndIdentifiersIdentifierSystemSystem(patientMrn, umsProperties.getMrn().getCodeSystem())
                .map(Demographics::getPatient)
                .orElseThrow(() -> new PatientNotFoundException("Patient Not Found!"));
        List<UserPatientRelationship> userPatientRelationshipList = userPatientRelationshipRepository.findAllByIdUserIdAndIdPatientId(user.getId(), patient.getId());

        if (userPatientRelationshipList == null || userPatientRelationshipList.size() < 1) {
            return new AccessDecisionDto(false);
        } else
            return new AccessDecisionDto(true);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(Long userId) {
        final User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByUserAuthId(String userAuthId) {
        final User user = userRepository.findByUserAuthIdAndDisabled(userAuthId, false)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!"));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Optional<Integer> page, Optional<Integer> size, Optional<String> roleCode) {
        final PageRequest pageRequest = new PageRequest(page.filter(p -> p >= 0).orElse(0),
                size.filter(s -> s > 0 && s <= umsProperties.getPagination().getMaxSize())
                        .orElse(umsProperties.getPagination().getDefaultSize()));
        Page<User> usersPage;
        if (roleCode.isPresent())
            usersPage = userRepository.findAllByRolesCode(roleCode.get(), pageRequest);
        else
            usersPage = userRepository.findAll(pageRequest);
        final List<User> userList = usersPage.getContent();
        final List<UserDto> getUserDtoList = userListToUserDtoList(userList);
        return new PageImpl<>(getUserDtoList, pageRequest, usersPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> searchUsersByDemographic(String firstName,
                                                  String lastName,
                                                  LocalDate birthDate,
                                                  String genderCode,
                                                  String mrn,
                                                  String roleCode,
                                                  Optional<Integer> page,
                                                  Optional<Integer> size) {
        final PageRequest pageRequest = new PageRequest(page.filter(p -> p >= 0).orElse(0),
                size.filter(s -> s > 0 && s <= umsProperties.getPagination().getMaxSize())
                        .orElse(umsProperties.getPagination().getDefaultSize()));
        final AdministrativeGenderCode administrativeGenderCode = administrativeGenderCodeRepository.findByCode(genderCode);
        Role patientRole = null;
        if (roleCode != null)
            patientRole = roleRepository.findByCode(roleCode);
        Identifier patientIdentifier = null;
        if (mrn != null) {
            if (!identifierRepository.findByValueAndIdentifierSystem(mrn, identifierSystemRepository.findBySystem(umsProperties.getMrn().getCodeSystem()).get()).isPresent())
                return new PageImpl<>(new ArrayList<UserDto>(), pageRequest, 0);
            else
                patientIdentifier = identifierRepository.findByValueAndIdentifierSystem(mrn, identifierSystemRepository.findBySystem(umsProperties.getMrn().getCodeSystem()).get()).get();
        }
        Page<Demographics> demographicsPage = demographicsRepository.query(firstName, lastName, administrativeGenderCode, birthDate, patientIdentifier, patientRole, pageRequest);

        List<Demographics> demographicsesList = demographicsPage.getContent();

        final List<UserDto> getUserDtoList = demographicsesListToUserDtoList(demographicsesList);
        return new PageImpl<>(getUserDtoList, pageRequest, demographicsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> searchUsersByIdentifier(String value, String system) {
        return userRepository.findAllByDemographicsIdentifiersValueAndDemographicsIdentifiersIdentifierSystemSystem(value, system)
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> searchUsersByFirstNameAndORLastName(StringTokenizer token) {
        Pageable pageRequest = new PageRequest(PAGE_NUMBER, umsProperties.getPagination().getDefaultSize());
        if (token.countTokens() == 1) {
            String firstName = token.nextToken(); // First Token could be first name or the last name
            return demographicsRepository.findAllByFirstNameLikesOrLastNameLikes("%" + firstName + "%", pageRequest)
                    .stream()
                    .map(demographics -> modelMapper.map(demographics.getUser(), UserDto.class))
                    .collect(toList());
        } else if (token.countTokens() >= 2) {
            String firstName = token.nextToken(); // First Token is the first name
            String lastName = token.nextToken();  // Last Token is the last name
            return demographicsRepository.findAllByFirstNameLikesAndLastNameLikes("%" + firstName + "%", "%" + lastName + "%", pageRequest)
                    .stream()
                    .map(demographics -> modelMapper.map(demographics.getUser(), UserDto.class))
                    .collect(toList());
        } else {
            return new ArrayList<>();
        }
    }

    private Set<UmsProperties.RequiredIdentifierSystem> getRequiredAndSystemGeneratedIdentifierSystems(Set<UmsProperties.RequiredIdentifierSystem> allRequiredIdentifierSystems) {
        return allRequiredIdentifierSystems.stream()
                .filter(requiredIdentifierSystem -> !requiredIdentifierSystem.getAlgorithm().equals(UmsProperties.Algorithm.NONE))
                .collect(toSet());
    }

    private Set<UmsProperties.RequiredIdentifierSystem> getAllRequiredIdentifierSystems(User user) {
        return umsProperties.getRequiredIdentifierSystemsByRole().entrySet().stream()
                .filter(entry -> user.getRoles().stream()
                        .map(Role::getCode)
                        .anyMatch(roleCode -> roleCode.equals(entry.getKey())))
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .collect(toSet());
    }

    private List<IdentifierDto> getConsolidatedIdentifierDtos(UserDto userDto, Set<UmsProperties.RequiredIdentifierSystem> systemGeneratedIdentifierSystems) {
        final boolean hasSsnInIdentifiers = userDto.getIdentifiers().orElseGet(Collections::emptyList).stream().anyMatch(identifierDto -> umsProperties.getSsn().getCodeSystem().equals(identifierDto.getSystem()));
        return Stream.concat(
                userDto.getIdentifiers().orElseGet(Collections::emptyList).stream(),
                // Convert SSN property to IdentifierDto to combine with other identifiers
                userDto.getSocialSecurityNumber()
                        // ignore SSN property if SSN exists in identifiers list
                        .filter(ssnValue -> !hasSsnInIdentifiers)
                        .map(ssnValue -> IdentifierDto.of(ssnValue, umsProperties.getSsn().getCodeSystem()))
                        .map(Stream::of)
                        .orElseGet(Stream::empty))
                .peek(idDto -> assertIdentifierSystemIsNotSystemGenerated(idDto, systemGeneratedIdentifierSystems))
                .collect(toList());
    }

    private Identifier createIdentifier(IdentifierDto idDto, Set<UmsProperties.RequiredIdentifierSystem> systemGeneratedIdentifierSystems) {
        return Identifier.of(idDto.getValue(), identifierSystemRepository
                .findBySystem(idDto.getSystem())
                .filter(identifierSystem -> systemGeneratedIdentifierSystems.stream()
                        .map(UmsProperties.RequiredIdentifierSystem::getSystem)
                        .noneMatch(identifierSystem.getSystem()::equals))
                .orElseThrow(() -> new InvalidIdentifierSystemException("Identifier System '" + idDto.getSystem() + "' is not found or it can be only generated by the system")));
    }

    private void assertIdentifierSystemIsNotSystemGenerated(IdentifierDto identifierDto, Set<UmsProperties.RequiredIdentifierSystem> systemGeneratedIdentifierSystems) {
        if (systemGeneratedIdentifierSystems.stream()
                .map(UmsProperties.RequiredIdentifierSystem::getSystem)
                .anyMatch(identifierDto.getSystem()::equals)) {
            final String errMsg = new StringBuilder()
                    .append("Identifier System '")
                    .append(identifierDto.getSystem())
                    .append("' can only be generated by the system")
                    .toString();
            throw new InvalidIdentifierSystemException(errMsg);
        }
    }

    private void assertDoesNotContainUnassignableIdentifiers(User user, List<Identifier> identifiersToAdd) {
        if (identifiersToAdd.stream()
                .filter(identifier -> Boolean.FALSE.equals(identifier.getIdentifierSystem().getReassignable()))
                .map(Identifier::getDemographics)
                .filter(Objects::nonNull)
                .anyMatch(demographics -> !user.getDemographics().equals(demographics))) {
            throw new UnassignableIdentifierException();
        }
    }

    private Patient createPatient(User user, Optional<String> registrationPurposeEmail) {
        //set the patient object
        Patient patient = new Patient();
        final Demographics demographics = user.getDemographics();
        patient.setDemographics(demographics);
        registrationPurposeEmail
                .filter(StringUtils::hasText)
                .map(String::trim)
                .ifPresent(patient::setRegistrationPurposeEmail);
        return patientRepository.save(patient);
    }

    private void createUserPatientRelationship(long userId, long patientId, String role) {
        RelationDto relationDto = new RelationDto(userId, patientId, role);
        UserPatientRelationship userPatientRelationship = new UserPatientRelationship();
        userPatientRelationship.setId(modelMapper.map(relationDto, UserPatientRelationshipId.class));
        userPatientRelationshipRepository.save(userPatientRelationship);
    }

    private boolean deepEquals(Identifier id, IdentifierDto idDto) {
        return id.getValue().equals(idDto.getValue()) && id.getIdentifierSystem().getSystem().equals(idDto.getSystem());
    }

    private Address mapAddressDtoToAddress(Address address, AddressDto addressDto) {
        address.setCity(addressDto.getCity());
        address.setStateCode(stateCodeRepository.findByCode(addressDto.getStateCode()));
        address.setCountryCode(countryCodeRepository.findByCode(addressDto.getCountryCode()));
        address.setLine1(addressDto.getLine1());
        address.setLine2(addressDto.getLine2());
        address.setPostalCode(addressDto.getPostalCode());
        if (addressDto.getUse().equals(Address.Use.HOME.toString()))
            address.setUse(Address.Use.HOME);
        if (addressDto.getUse().equals(Address.Use.WORK.toString()))
            address.setUse(Address.Use.WORK);
        return address;
    }

    private Telecom mapTelecomDtoToTelcom(Telecom telecom, TelecomDto telecomDto) {
        telecom.setValue(telecomDto.getValue());

        if (telecomDto.getUse().equals(Telecom.Use.HOME.toString()))
            telecom.setUse(Telecom.Use.HOME);
        if (telecomDto.getUse().equals(Telecom.Use.WORK.toString()))
            telecom.setUse(Telecom.Use.WORK);

        if (telecomDto.getSystem().equals(Telecom.System.EMAIL.toString()))
            telecom.setSystem(Telecom.System.EMAIL);
        if (telecomDto.getSystem().equals(Telecom.System.PHONE.toString()))
            telecom.setSystem(Telecom.System.PHONE);

        return telecom;
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
        userRepository.findById(userId)
                .map(User::getUserAuthId)
                .filter(StringUtils::hasText)
                .orElseThrow(UserActivationNotFoundException::new);
    }

    private void assertAllRequiredIdentifiersExist(List<Identifier> identifiers, Set<UmsProperties.RequiredIdentifierSystem> allRequiredIdentifierSystems) {
        final Set<String> missingRequiredIdentifiersSystems = allRequiredIdentifierSystems.stream()
                .map(UmsProperties.RequiredIdentifierSystem::getSystem)
                .filter(system -> identifiers.stream()
                        .map(Identifier::getIdentifierSystem)
                        .map(IdentifierSystem::getSystem)
                        .noneMatch(system::equals))
                .collect(toSet());
        if (missingRequiredIdentifiersSystems.size() > 0) {
            throw new InvalidIdentifierSystemException("Missing identifiers for the required identifier systems: " + missingRequiredIdentifiersSystems.toString());
        }
    }

    private String generateIdentifier(UmsProperties.Algorithm algorithm) {
        switch (algorithm) {
            case MRN:
                return mrnService.generateMrn();
            case UUID:
                return UUID.randomUUID().toString();
            case NONE:
            default:
                throw new InvalidIdentifierSystemException("This identifier system is not configured with an algorithm, the identifier cannot be generated");
        }
    }

    private void assertEmails(UserDto userDto, boolean patientPresent) {
        final boolean userHasEmail = userDto.getTelecoms().stream()
                .anyMatch(telecom -> telecom.getSystem().equals(Telecom.System.EMAIL.toString()));
        final boolean hasRegistrationPurposeEmail = userDto.getRegistrationPurposeEmail().filter(StringUtils::hasText).isPresent();
        if ((!userHasEmail) && (patientPresent && !hasRegistrationPurposeEmail)) {
            throw new MissingEmailException("At least one of personal email OR a registration purpose email is required");
        }
    }

    private boolean deepEquals(Telecom telecom, TelecomDto telecomDto) {
        return telecom.getSystem().toString().equals(telecomDto.getSystem()) &&
                telecom.getValue().equals(telecomDto.getValue()) &&
                telecom.getUse().toString().equals(telecomDto.getUse());
    }
}
