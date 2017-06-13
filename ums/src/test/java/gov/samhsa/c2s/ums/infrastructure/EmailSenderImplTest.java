package gov.samhsa.c2s.ums.infrastructure;

import gov.samhsa.c2s.ums.config.EmailSenderProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TemplateEngine.class)
public class EmailSenderImplTest {
    private static final String TEMPLATE_VERIFICATION_LINK_EMAIL = "verification-link-email";
    private static final String PROP_EMAIL_VERIFICATION_LINK_SUBJECT = "email.verificationLink.subject";

    private static final String PROP_EMAIL_CONFIRM_VERIFICATION_SUBJECT = "email.confirmVerification.subject";
    private static final String TEMPLATE_CONFIRM_VERIFICATION_EMAIL = "confirm-verification-email";
    private static final String PROP_EMAIL_FROM_ADDRESS = "email.from.address";
    private static final String PROP_EMAIL_FROM_PERSONAL = "email.from.personal";
    private static final String PROP_EMAIL_VERIFICATION_SIGN_OFF = "email.verification.body.signOff";
    private static final String PROP_EMAIL_VERIFICATION_CREATE_LOGIN_LINK = "email.verification.body.createLoginlink";
    private static final String PROP_EMAIL_VERIFICATION_MESSAGE = "email.verification.body.message";
    private static final String PROP_EMAIL_VERIFICATION_GREETING = "email.verification.body.greeting";
    private static final String PROP_EMAIL_VERIFICATION_BODY_HEADER = "email.verification.body.header";

    private static final String PROP_EMAIL_CONFIRM_VERIFICATION_SIGN_OFF = "email.confirmVerification.body.signOff";
    private static final String PROP_EMAIL_CONFIRM_VERIFICATION_LOGIN_LINK = "email.confirmVerification.body.loginLink";
    private static final String PROP_EMAIL_CONFIRM_VERIFICATION_MESSAGE1 = "email.confirmVerification.body.message1";
    private static final String PROP_EMAIL_CONFIRM_VERIFICATION_MESSAGE2 = "email.confirmVerification.body.message2";
    private static final String PROP_EMAIL_CONFIRM_VERIFICATION_GREETING = "email.confirmVerification.body.greeting";
    private static final String PROP_EMAIL_CONFIRM_VERIFICATION_BODY_HEADER = "email.confirmVerification.body.header";

    @Mock
    EmailSenderProperties emailSenderProperties;

    @Mock
    JavaMailSender javaMailSender;

    TemplateEngine templateEngine;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    EmailSenderImpl emailSenderImpl;

    @Test
    public void testSendEmailWithVerificationLink(){
        //Arrange
        String xForwardedProto="xForwardedProto";
        String xForwardedHost="xForwardedHost";
        int xForwardedPort=234;
        String email="email";
        String emailToken="emailToken";
        String recipientFullName="recipientFullName";
        String emailTokenArgName="emailTokenArgName";
        String localeArgName="localArgName";
        String htmlContent="htmlContent";

        Locale locale=new Locale("English");

        when(emailSenderProperties.getC2sUiVerificationEmailTokenArgName()).thenReturn(emailTokenArgName);
        when(emailSenderProperties.getC2sUiVerificationUserPreferredLocaleArgName()).thenReturn(localeArgName);

        when(messageSource.getMessage(PROP_EMAIL_VERIFICATION_BODY_HEADER,null,locale)).thenReturn("verificationBody");
        when(messageSource.getMessage(PROP_EMAIL_VERIFICATION_GREETING,null,locale)).thenReturn("verificationGreeting");
        when(messageSource.getMessage(PROP_EMAIL_VERIFICATION_MESSAGE,null,locale)).thenReturn("emailVerificationMessage");
        when(messageSource.getMessage(PROP_EMAIL_VERIFICATION_CREATE_LOGIN_LINK,null,locale)).thenReturn("createLoginLink");
        when(messageSource.getMessage(PROP_EMAIL_VERIFICATION_SIGN_OFF,null,locale)).thenReturn("emailVerificationSignOff");

        when(emailSenderProperties.getBrand()).thenReturn("brand");
        when(emailSenderProperties.getC2sUiRoute()).thenReturn("C2SUiRoute");
        when(emailSenderProperties.getC2sUiVerificationRelativePath()).thenReturn("relation");

        MimeMessage mimeMessage=mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(messageSource.getMessage(PROP_EMAIL_VERIFICATION_LINK_SUBJECT,null,locale)).thenReturn("verificationLink");
        when(messageSource.getMessage(PROP_EMAIL_FROM_ADDRESS,null,locale)).thenReturn("fromAddress");
        when(messageSource.getMessage(PROP_EMAIL_FROM_PERSONAL,null,locale)).thenReturn("personal");

        templateEngine = PowerMockito.mock(TemplateEngine.class);
        PowerMockito.doReturn(htmlContent).when(templateEngine).process(eq(TEMPLATE_VERIFICATION_LINK_EMAIL), any(Context.class));
        ReflectionTestUtils.setField(emailSenderImpl, "templateEngine", templateEngine);

        //Act
        emailSenderImpl.sendEmailWithVerificationLink(xForwardedProto,xForwardedHost,xForwardedPort,email,emailToken,recipientFullName,locale);

        //Assert
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    public void testSendEmailToConfirmVerification(){
        //Arrange
        String xForwardedProto="xForwardedProto";
        String xForwardedHost="xForwardedHost";
        int xForwardedPort=234;
        String email="email";
        String emailToken="emailToken";
        String recipientFullName="recipientFullName";
        String emailTokenArgName="emailTokenArgName";
        String localeArgName="localArgName";
        String htmlContent="htmlContent";

        Locale locale=new Locale("English");
        when(emailSenderProperties.getC2sUiRoute()).thenReturn("C2sUiRoute");

        when(messageSource.getMessage(PROP_EMAIL_CONFIRM_VERIFICATION_BODY_HEADER,null,locale)).thenReturn("verificationBody");
        when(messageSource.getMessage(PROP_EMAIL_CONFIRM_VERIFICATION_GREETING,null,locale)).thenReturn("verificationGreeting");
        when(messageSource.getMessage(PROP_EMAIL_CONFIRM_VERIFICATION_MESSAGE1,null,locale)).thenReturn("emailVerificationMessage1");
        when(messageSource.getMessage(PROP_EMAIL_CONFIRM_VERIFICATION_MESSAGE2,null,locale)).thenReturn("emailVerificationMessage2");
        when(messageSource.getMessage(PROP_EMAIL_CONFIRM_VERIFICATION_LOGIN_LINK,null,locale)).thenReturn("createLoginLink");
        when(messageSource.getMessage(PROP_EMAIL_CONFIRM_VERIFICATION_SIGN_OFF,null,locale)).thenReturn("emailVerificationSignOff");

        when(emailSenderProperties.getBrand()).thenReturn("brand");

        MimeMessage mimeMessage=mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(messageSource.getMessage(PROP_EMAIL_CONFIRM_VERIFICATION_SUBJECT,null,locale)).thenReturn("verificationLink");
        when(messageSource.getMessage(PROP_EMAIL_FROM_ADDRESS,null,locale)).thenReturn("fromAddress");
        when(messageSource.getMessage(PROP_EMAIL_FROM_PERSONAL,null,locale)).thenReturn("personal");

        templateEngine = PowerMockito.mock(TemplateEngine.class);
        PowerMockito.doReturn(htmlContent).when(templateEngine).process(eq(TEMPLATE_CONFIRM_VERIFICATION_EMAIL), any(Context.class));
        ReflectionTestUtils.setField(emailSenderImpl, "templateEngine", templateEngine);

        //Act
        emailSenderImpl.sendEmailToConfirmVerification(xForwardedProto,xForwardedHost,xForwardedPort,email,recipientFullName,locale);

        //Assert
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }
}
