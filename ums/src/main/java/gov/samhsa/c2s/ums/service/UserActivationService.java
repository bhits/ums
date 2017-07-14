package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.service.dto.ScopeAssignmentRequestDto;
import gov.samhsa.c2s.ums.service.dto.ScopeAssignmentResponseDto;
import gov.samhsa.c2s.ums.service.dto.UserActivationRequestDto;
import gov.samhsa.c2s.ums.service.dto.UserActivationResponseDto;
import gov.samhsa.c2s.ums.service.dto.UserVerificationRequestDto;
import gov.samhsa.c2s.ums.service.dto.UsernameUsedDto;
import gov.samhsa.c2s.ums.service.dto.VerificationResponseDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserActivationService {

    @Transactional
    UserActivationResponseDto initiateUserActivation(Long userId, String xForwardedProto, String xForwardedHost, int xForwardedPort, Optional<String> lastUpdatedBy);

    @Transactional(readOnly = true)
    UserActivationResponseDto findUserActivationInfoByUserId(Long userId);


    @Transactional
    UserActivationResponseDto activateUser(UserActivationRequestDto userActivationRequest, String xForwardedProto, String xForwardedHost, int xForwardedPort);

    @Transactional(readOnly = true)
    VerificationResponseDto verify(UserVerificationRequestDto userVerificationRequest);

    @Transactional
    ScopeAssignmentResponseDto assignScopeToUser(ScopeAssignmentRequestDto scopeAssignmentRequestDto);

    @Transactional
    UsernameUsedDto checkUsername(String username);

}