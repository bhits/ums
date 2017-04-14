package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.dto.ScopeAssignmentRequestDto;
import gov.samhsa.c2s.ums.service.dto.ScopeAssignmentResponseDto;
import gov.samhsa.c2s.ums.service.dto.UserActivationRequestDto;
import gov.samhsa.c2s.ums.service.dto.UserActivationResponseDto;
import gov.samhsa.c2s.ums.service.dto.VerificationResponseDto;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

public interface UserActivationService {

    @Transactional
    UserActivationResponseDto initiateUserActivation(Long userId, String xForwardedProto, String xForwardedHost, int xForwardedPort);

    @Transactional(readOnly = true)
    UserActivationResponseDto findUserActivationInfoByUserId(Long userId);


    @Transactional
    UserActivationResponseDto activateUser(UserActivationRequestDto userActivationRequest, String xForwardedProto, String xForwardedHost, int xForwardedPort);

    @Transactional(readOnly = true)
    VerificationResponseDto verify(String emailToken, Optional<String> verificationCode, Optional<LocalDate> birthDate);

    @Transactional
    ScopeAssignmentResponseDto assignScopeToUser(ScopeAssignmentRequestDto scopeAssignmentRequestDto);

}