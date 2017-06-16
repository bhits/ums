package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.common.util.UniqueValueGeneratorException;
import gov.samhsa.c2s.ums.domain.UserActivation;
import gov.samhsa.c2s.ums.domain.UserActivationRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmailTokenGeneratorImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private UserActivationRepository userActivationRepository;

    @InjectMocks
    EmailTokenGeneratorImpl emailTokenGeneratorImpl;

    @Test
    public void testGenerateEmailToken_Given_TheUniqueTokenIsGenerated() {
        //Arrange
        String token = "token";
        when(tokenGenerator.generateToken()).thenReturn(token);
        when(userActivationRepository.findOneByEmailToken(token)).thenReturn(Optional.empty());

        //Act
        String generatedEmailToken = emailTokenGeneratorImpl.generateEmailToken();

        //Assert
        assertEquals(token, generatedEmailToken);
    }
}