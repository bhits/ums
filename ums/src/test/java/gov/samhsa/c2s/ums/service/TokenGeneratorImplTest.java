package gov.samhsa.c2s.ums.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.StringUtils;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TokenGeneratorImplTest {

    TokenGeneratorImpl tokenGeneratorImpl=new TokenGeneratorImpl();

    @Test
    public void generateToken_withoutPassingMaxLength(){
        //Act
        String token=tokenGeneratorImpl.generateToken();

        //assert
        assertTrue(StringUtils.hasText(token));
    }

    @Test
    public void generateToken_PassingMaxLength(){
        //Act
        int length=7;
        String token=tokenGeneratorImpl.generateToken(length);

        //assert
        assertTrue(StringUtils.hasText(token));
        assertTrue(token.length()<=length);

    }
}
