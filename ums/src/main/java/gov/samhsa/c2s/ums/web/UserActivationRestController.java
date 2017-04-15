package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.UserActivationService;
import gov.samhsa.c2s.ums.service.dto.ScopeAssignmentRequestDto;
import gov.samhsa.c2s.ums.service.dto.ScopeAssignmentResponseDto;
import gov.samhsa.c2s.ums.service.dto.UserActivationRequestDto;
import gov.samhsa.c2s.ums.service.dto.UserActivationResponseDto;
import gov.samhsa.c2s.ums.service.dto.UserVerificationRequestDto;
import gov.samhsa.c2s.ums.service.dto.VerificationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserActivationRestController {

    public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    public static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    public static final String X_FORWARDED_PORT = "X-Forwarded-Port";


    @Autowired
    UserActivationService userActivationService;

    @PostMapping(value = "/{userId}/activation")
    public UserActivationResponseDto initiateUserActivation(@PathVariable Long userId,
                                                        @RequestHeader(X_FORWARDED_PROTO) String xForwardedProto,
                                                        @RequestHeader(X_FORWARDED_HOST) String xForwardedHost,
                                                        @RequestHeader(X_FORWARDED_PORT) int xForwardedPort) {
        final UserActivationResponseDto userActivationResponseDto = userActivationService.initiateUserActivation(userId, xForwardedProto, xForwardedHost, xForwardedPort);
        return userActivationResponseDto;
    }

    @GetMapping(value = "/{userId}/activation")
    public UserActivationResponseDto getCurrentUserCreationInfo(@PathVariable Long userId) {
        return userActivationService.findUserActivationInfoByUserId(userId);
    }



    @PostMapping(value = "/activation")
    public UserActivationResponseDto activateUser(@Valid @RequestBody UserActivationRequestDto userActivationRequest,
                                                  @RequestHeader(X_FORWARDED_PROTO) String xForwardedProto,
                                                  @RequestHeader(X_FORWARDED_HOST) String xForwardedHost,
                                                  @RequestHeader(X_FORWARDED_PORT) int xForwardedPort) {
        return userActivationService.activateUser(userActivationRequest, xForwardedProto, xForwardedHost, xForwardedPort);
    }



    @PostMapping(value = "/verification")
    public VerificationResponseDto verify(@Valid @RequestBody UserVerificationRequestDto userVerificationRequest) {
        return userActivationService.verify(userVerificationRequest);
    }

    @PostMapping(value = "/scopeAssignments")
    public ScopeAssignmentResponseDto assignScope(@Valid @RequestBody ScopeAssignmentRequestDto scopeAssignmentRequestDto) {
        return userActivationService.assignScopeToUser(scopeAssignmentRequestDto);
    }
}
