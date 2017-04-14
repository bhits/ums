package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.config.EmailSenderProperties;
import gov.samhsa.c2s.ums.domain.RoleRepository;
import gov.samhsa.c2s.ums.domain.Scope;
import gov.samhsa.c2s.ums.domain.ScopeRepository;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserActivation;
import gov.samhsa.c2s.ums.domain.UserActivationRepository;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.domain.UserScopeAssignment;
import gov.samhsa.c2s.ums.domain.UserScopeAssignmentRepository;
import gov.samhsa.c2s.ums.infrastructure.EmailSender;
import gov.samhsa.c2s.ums.infrastructure.ScimService;
import gov.samhsa.c2s.ums.service.dto.ScopeAssignmentRequestDto;
import gov.samhsa.c2s.ums.service.dto.ScopeAssignmentResponseDto;
import gov.samhsa.c2s.ums.service.dto.UserActivationRequestDto;
import gov.samhsa.c2s.ums.service.dto.UserActivationResponseDto;
import gov.samhsa.c2s.ums.service.dto.VerificationResponseDto;
import gov.samhsa.c2s.ums.service.exception.EmailTokenExpiredException;
import gov.samhsa.c2s.ums.service.exception.PasswordConfirmationFailedException;
import gov.samhsa.c2s.ums.service.exception.ScopeDoesNotExistInDBException;
import gov.samhsa.c2s.ums.service.exception.UserActivationCannotBeVerifiedException;
import gov.samhsa.c2s.ums.service.exception.UserActivationNotFoundException;
import gov.samhsa.c2s.ums.service.exception.UserIsAlreadyVerifiedException;
import gov.samhsa.c2s.ums.service.exception.VerificationFailedException;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserActivationServiceImpl implements UserActivationService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private UserActivationRepository userActivationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailTokenGenerator emailTokenGenerator;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private ScimService scimService;

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private EmailSenderProperties emailSenderProperties;

    @Autowired
    private UserScopeAssignmentRepository userScopeAssignmentRepository;

    @Override
    public UserActivationResponseDto initiateUserActivation(Long userId, String xForwardedProto, String xForwardedHost, int xForwardedPort) {
        // Find user
        final User user = userRepository.getOne(userId);

        String emailToken = emailTokenGenerator.generateEmailToken();
        final Instant emailTokenExpirationDate = Instant.now().plus(Period.ofDays(emailSenderProperties.getEmailTokenExpirationInDays()));
        final UserActivation userActivation = userActivationRepository.findOneByUserId(userId)
                .orElseGet(UserActivation::new);
        assertNotAlreadyVerified(userActivation);
        userActivation.setEmailTokenExpirationAsInstant(emailTokenExpirationDate);
        userActivation.setEmailToken(emailToken);
        userActivation.setUser(user);
        userActivation.setVerified(false);
        userActivation.setVerificationCode(tokenGenerator.generateToken(7));
        // Persists record
        final UserActivation saved = userActivationRepository.save(userActivation);
        // Prepare response for the patient user creation
        final UserActivationResponseDto response = modelMapper.map(user, UserActivationResponseDto.class);
        response.setBirthDate(user.getBirthDay());
        response.setVerificationCode(saved.getVerificationCode());
        response.setEmailTokenExpiration(saved.getEmailTokenExpirationAsInstant());
        response.setVerified(saved.isVerified());
        // Send email with verification link
        emailSender.sendEmailWithVerificationLink(
                xForwardedProto, xForwardedHost, xForwardedPort,
                user.getTelecoms().get(0).getValue(),
                saved.getEmailToken(),
                getRecipientFullName(user));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public VerificationResponseDto verify(String emailToken, Optional<String> verificationCode, Optional<LocalDate> birthDate) {

            Assert.hasText(emailToken, "emailToken must have text");
            final Instant now = Instant.now();
            // Only emailToken is available
            if (!verificationCode.isPresent() && !birthDate.isPresent()) {
                final Optional<UserActivation> userActivationOptional = userActivationRepository.findOneByEmailToken(emailToken);
                if (userActivationOptional.filter(uc -> uc.isVerified() == true).isPresent()) {
                    throw new UserIsAlreadyVerifiedException();
                }
                final Boolean verified = userActivationRepository.findOneByEmailToken(emailToken)
                        .map(UserActivation::getEmailTokenExpirationAsInstant)
                        .map(expiration -> expiration.isAfter(now))
                        .filter(Boolean.TRUE::equals)
                        .orElseThrow(VerificationFailedException::new);
                return new VerificationResponseDto(verified);
            } else {
                // All arguments must be available
                final String verificationCodeNullSafe = verificationCode.filter(StringUtils::hasText).orElseThrow(VerificationFailedException::new);
                final LocalDate birthDateNullSafe = birthDate.filter(Objects::nonNull).orElseThrow(VerificationFailedException::new);
                // Assert user activation email token
                assertEmailTokenNotExpired(userActivationRepository.findOneByEmailToken(emailToken).get());
                User user = userActivationRepository.findOneByEmailToken(emailToken).get().getUser();
                final Long userId = userActivationRepository
                        .findOneByEmailTokenAndVerificationCode(emailToken, verificationCodeNullSafe)
                        .filter(uc -> uc.getEmailTokenExpirationAsInstant().isAfter(now))
                        .map(UserActivation::getUser)
                        .map(User::getId)
                        .orElseThrow(VerificationFailedException::new);
                final boolean verified = Optional.of(user)
                        .map(User::getBirthDay)
                        .map(birthDateNullSafe::equals)
                        .filter(Boolean.TRUE::equals)
                        .orElseThrow(VerificationFailedException::new);
                return new VerificationResponseDto(verified, userId.toString());
            }

    }

    @Override
    @Transactional(readOnly = true)
    public UserActivationResponseDto findUserActivationInfoByUserId(Long userId) {
        final User user = userRepository.findOne(userId);
        final UserActivation userActivation = userActivationRepository.findOneByUserId(userId).orElseThrow(() -> new UserActivationNotFoundException("No user activation record found for user id: " + userId));
        final UserActivationResponseDto response = modelMapper.map(user, UserActivationResponseDto.class);
        response.setBirthDate(user.getBirthDay());
        response.setVerificationCode(userActivation.getVerificationCode());
        response.setEmailTokenExpiration(userActivation.getEmailTokenExpirationAsInstant());
        response.setVerified(userActivation.isVerified());
        return response;
    }

    @Override
    public UserActivationResponseDto activateUser(UserActivationRequestDto userActivationRequest, String xForwardedProto, String xForwardedHost, int xForwardedPort) {
        // Verify password
        assertPasswordAndConfirmPassword(userActivationRequest);
        // Find user creation process with emailToken and verificationCode
        final UserActivation userActivation = userActivationRepository.findOneByEmailTokenAndVerificationCode(
                userActivationRequest.getEmailToken(),
                userActivationRequest.getVerificationCode())
                .orElseThrow(UserActivationCannotBeVerifiedException::new);
        final User user = userActivation.getUser();

        // Assert user creation process preconditions
        assertNotAlreadyVerified(userActivation);
        assertEmailTokenNotExpired(userActivation);
        // Assert birth date verification
        assertBirthDateVerification(userActivationRequest, user);
        userActivation.setVerified(true);
        userActivationRepository.save(userActivation);
        // Prepare response
        final UserActivationResponseDto response = modelMapper.map(user, UserActivationResponseDto.class);
        response.setBirthDate(user.getBirthDay());
        response.setVerified(userActivation.isVerified());
        // Create user using SCIM
        ScimUser scimUser = new ScimUser(null, user.getTelecoms().get(0).getValue(), user.getFirstName(), user.getLastName());
        scimUser.setPassword(userActivationRequest.getPassword());
        ScimUser.Email email = new ScimUser.Email();
        email.setValue(user.getTelecoms().get(0).getValue());
        scimUser.setEmails(Collections.singletonList(email));
        scimUser.setVerified(true);
        // Save SCIM user
        final ScimUser savedScimUser = scimService.save(scimUser);
        final String userId = savedScimUser.getId();
        Assert.hasText(userId, "SCIM userId must have text");
        // Save userId in userActivation
        user.setOauth2UserId(userId);
        userActivationRepository.save(userActivation);
        // Add user to groups
        scimService.addUserToGroups(userActivation);
        emailSender.sendEmailToConfirmVerification(
                xForwardedProto, xForwardedHost, xForwardedPort,
                user.getTelecoms().get(0).getValue(),
                getRecipientFullName(user));
        return response;
        //return new UserActivationResponseDto();
    }

    private void assertPasswordAndConfirmPassword(UserActivationRequestDto userActivationRequest) {
        if (!userActivationRequest.getPassword().equals(userActivationRequest.getConfirmPassword())) {
            throw new PasswordConfirmationFailedException();
        }
    }

    private void assertNotAlreadyVerified(UserActivation userActivation) {
        if (userActivation.isVerified()) {
            throw new UserIsAlreadyVerifiedException();
        }
    }

    private void assertEmailTokenNotExpired(UserActivation userActivation) {
        if (userActivation.getEmailTokenExpirationAsInstant().isBefore(Instant.now())) {
            throw new EmailTokenExpiredException();
        }
    }

    private void assertBirthDateVerification(UserActivationRequestDto userActivationRequest, User user) {
        final LocalDate birthDayInRequest = userActivationRequest.getBirthDate();
        final LocalDate birthDayInUser = LocalDate.from(user.getBirthDay());
        if (!birthDayInUser.equals(birthDayInRequest)) {
            throw new UserActivationCannotBeVerifiedException();
        }
    }

    private String getRecipientFullName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    public ScopeAssignmentResponseDto assignScopeToUser(ScopeAssignmentRequestDto scopeAssignmentRequestDto) {
        scopeAssignmentRequestDto.getScopes().stream()
                .forEach(scope -> {
                    Scope foundScope = Optional.ofNullable(scopeRepository.findByScopeName(scope)).orElseThrow(ScopeDoesNotExistInDBException::new);
                    assignNewScopesToUsers(foundScope);
                });
        return null;
    }

    private void assignNewScopesToUsers(Scope scope) {
        userActivationRepository.findAll().stream()
                .forEach(userActivation -> {
                    UserScopeAssignment userScopeAssignment = new UserScopeAssignment();
                    userScopeAssignment.setScope(scope);
                    userScopeAssignment.setUserActivation(userActivation);
                    try {
                        userScopeAssignment.setAssigned(true);
                        userScopeAssignmentRepository.save(userScopeAssignment);
                        scimService.updateUserWithNewGroup(userActivation, scope);
                    } catch (Exception e) {
                        userScopeAssignment.setAssigned(false);
                        userScopeAssignmentRepository.save(userScopeAssignment);
                    }
                });
    }

}