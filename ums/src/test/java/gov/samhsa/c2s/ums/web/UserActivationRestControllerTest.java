package gov.samhsa.c2s.ums.web;

import gov.samhsa.c2s.ums.service.UserActivationService;
import gov.samhsa.c2s.ums.service.dto.ScopeAssignmentRequestDto;
import gov.samhsa.c2s.ums.service.dto.ScopeAssignmentResponseDto;
import gov.samhsa.c2s.ums.service.dto.UserActivationRequestDto;
import gov.samhsa.c2s.ums.service.dto.UserActivationResponseDto;
import gov.samhsa.c2s.ums.service.dto.UserVerificationRequestDto;
import gov.samhsa.c2s.ums.service.dto.VerificationResponseDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserActivationRestControllerTest {

    public static final String xForwardProto = "X-Forwarded-Proto";
    public static final String xForwardHost = "X-Forwarded-Host";
    public static final int xForwardPort= 233;

    @Mock
    UserActivationService userActivationService;

    @InjectMocks
    UserActivationRestController userActivationRestController;

    @Test
    public void testInitiateUserActivation(){
        //Arrange
        long userId=30L;
        UserActivationResponseDto userActivationResponseDto=mock(UserActivationResponseDto.class);
        when(userActivationService.initiateUserActivation(userId,xForwardProto,xForwardHost,xForwardPort)).thenReturn(userActivationResponseDto);

        //Act
        UserActivationResponseDto userActivationResponseDto2=userActivationRestController.initiateUserActivation(userId,xForwardProto,xForwardHost,xForwardPort);

        //Assert
        assertEquals(userActivationResponseDto,userActivationResponseDto2);
    }

    @Test
    public void testGetCurrentUserCreationInfo(){
        //Arrange
        long userId=30L;
        UserActivationResponseDto userActivationResponseDto=mock(UserActivationResponseDto.class);
        when(userActivationService.findUserActivationInfoByUserId(userId)).thenReturn(userActivationResponseDto);

        //Act
        UserActivationResponseDto userActivationResponseDto2=userActivationRestController.getCurrentUserCreationInfo(userId);

        //Assert
        assertEquals(userActivationResponseDto,userActivationResponseDto2);
    }


    @Test
    public void testActivateUser(){
        //Arrange
        UserActivationRequestDto userActivationRequestDto=mock(UserActivationRequestDto.class);
        UserActivationResponseDto userActivationResponseDto=mock(UserActivationResponseDto.class);
        when(userActivationService.activateUser(userActivationRequestDto,xForwardProto,xForwardHost,xForwardPort)).thenReturn(userActivationResponseDto);

        //Act
        UserActivationResponseDto userActivationResponseDto2=userActivationRestController.activateUser(userActivationRequestDto,xForwardProto,xForwardHost,xForwardPort);


        //Assert
        assertEquals(userActivationResponseDto,userActivationResponseDto2);
    }

    @Test
    public void testVerify(){
        //Arrange
        VerificationResponseDto verificationResponseDto=mock(VerificationResponseDto.class);
        UserVerificationRequestDto userVerificationRequestDto=mock(UserVerificationRequestDto.class);
        when(userActivationService.verify(userVerificationRequestDto)).thenReturn(verificationResponseDto);

        //Act
        VerificationResponseDto verificationResponseDto2=userActivationRestController.verify(userVerificationRequestDto);

        //Assert
        assertEquals(verificationResponseDto,verificationResponseDto2);
    }

    @Test
    public void testAssignScope(){
        //Arrange
        ScopeAssignmentRequestDto scopeAssignmentRequestDto=mock(ScopeAssignmentRequestDto.class);
        ScopeAssignmentResponseDto scopeAssignmentResponseDto=mock(ScopeAssignmentResponseDto.class);
        when(userActivationService.assignScopeToUser(scopeAssignmentRequestDto)).thenReturn(scopeAssignmentResponseDto);

        //Act
        ScopeAssignmentResponseDto scopeAssignmentResponseDto2=userActivationRestController.assignScope(scopeAssignmentRequestDto);

        //Assert
        assertEquals(scopeAssignmentResponseDto,scopeAssignmentResponseDto2);


    }

}
