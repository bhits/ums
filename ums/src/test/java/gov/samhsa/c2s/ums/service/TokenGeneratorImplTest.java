package gov.samhsa.c2s.ums.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.StringUtils;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TokenGeneratorImplTest {

    TokenGeneratorImpl tokenGeneratorImpl = new TokenGeneratorImpl();

    @Test
    public void generateToken_Given_WithoutPassingMaxLength() {
        //Act
        String token = tokenGeneratorImpl.generateToken();

        //Assert
        assertTrue(StringUtils.hasText(token));
    }

    @Test
    public void generateToken_Given_MaxLength_Then_GenerateTokenNotLongerThanMaxLength() {
        //Act
        int length = 7;
        String token = tokenGeneratorImpl.generateToken(length);

        //Assert
        assertTrue(StringUtils.hasText(token));
        assertTrue(token.length() <= length);

    }
}
