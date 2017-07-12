package gov.samhsa.c2s.ums.infrastructure;

import gov.samhsa.c2s.ums.config.EmailSenderProperties;
import gov.samhsa.c2s.ums.infrastructure.exception.EmailSenderException;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class EmailSenderImpl implements EmailSender {

    // Verification link
    private static final String TEMPLATE_VERIFICATION_LINK_EMAIL = "verification-link-email";
    private static final String PROP_EMAIL_VERIFICATION_LINK_SUBJECT = "email.verificationLink.subject";

    // Confirm verification
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

    private static final String ENCODING = StandardCharsets.UTF_8.toString();

    private static final String PARAM_RECIPIENT_NAME = "recipientName";
    private static final String PARAM_LINK_URL = "linkUrl";

    private static final String PARAM_BRAND = "brand";
    private static final String PARAM_SIGN_OFF = "signOff";
    private static final String PARAM_VERIFICATION_CREATE_LOGIN_LINK = "createLoginlink";
    private static final String PARAM_VERIFICATION_MESSAGE = "message";
    private static final String PARAM_VERIFICATION_GREETING = "greeting";
    private static final String PARAM_VERIFICATION_HEADER = "header";


    private static final String PARAM_CONFIRM_VERIFICATION_HEADER = "header";
    private static final String PARAM_CONFIRM_VERIFICATION_GREETING = "greeting";
    private static final String PARAM_CONFIRM_VERIFICATION_MESSAGE1 = "message1";
    private static final String PARAM_CONFIRM_VERIFICATION_MESSAGE2 = "message2";
    private static final String PARAM_CONFIRM_VERIFICATION_LOGIN_LINK = "loginLink";
    private static final String PARAM_CONFIRM_SIGN_OFF = "signOff";

    @Autowired
    private EmailSenderProperties emailSenderProperties;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void sendEmailWithVerificationLink(String xForwardedProto, String xForwardedHost, int xForwardedPort, String email, String emailToken, String recipientFullName, Locale locale) {
        Assert.hasText(emailToken, "emailToken must have text");
        Assert.hasText(locale.getLanguage(), "defaultLocale must have text");
        Assert.hasText(email, "email must have text");
        Assert.hasText(recipientFullName, "recipientFullName must have text");
        final String fragment = emailSenderProperties.getC2sUiVerificationEmailTokenArgName().concat("=")
                + emailToken.concat("&") + emailSenderProperties.getC2sUiVerificationUserPreferredLocaleArgName().concat("=")
                + locale.getLanguage();

        final String verificationUrl = toC2SUIVerificationUri(xForwardedProto, xForwardedHost, xForwardedPort, fragment);
        final Context ctx = new Context();
        ctx.setLocale(locale);
        ctx.setVariable(PARAM_RECIPIENT_NAME, recipientFullName);
        ctx.setVariable(PARAM_LINK_URL, verificationUrl);
        ctx.setVariable(PARAM_BRAND, emailSenderProperties.getBrand());
        ctx.setVariable(PARAM_VERIFICATION_HEADER, messageSource.getMessage(PROP_EMAIL_VERIFICATION_BODY_HEADER, null, locale));
        ctx.setVariable(PARAM_VERIFICATION_GREETING, messageSource.getMessage(PROP_EMAIL_VERIFICATION_GREETING, null, locale));
        // Caution: this parameter injected to template without escaping. Do not set any user provided content here.
        ctx.setVariable(PARAM_VERIFICATION_MESSAGE, messageSource.getMessage(PROP_EMAIL_VERIFICATION_MESSAGE, null, locale));
        ctx.setVariable(PARAM_VERIFICATION_CREATE_LOGIN_LINK, messageSource.getMessage(PROP_EMAIL_VERIFICATION_CREATE_LOGIN_LINK, null, locale));
        ctx.setVariable(PARAM_SIGN_OFF, messageSource.getMessage(PROP_EMAIL_VERIFICATION_SIGN_OFF, null, locale));

        sendEmail(ctx, email,
                PROP_EMAIL_VERIFICATION_LINK_SUBJECT,
                TEMPLATE_VERIFICATION_LINK_EMAIL,
                PROP_EMAIL_FROM_ADDRESS,
                PROP_EMAIL_FROM_PERSONAL,
                locale);
    }

    @Override
    public void sendEmailToConfirmVerification(String xForwardedProto, String xForwardedHost, int xForwardedPort, String email, String recipientFullName, Locale locale) {
        Assert.hasText(email, "email must have text");
        Assert.hasText(recipientFullName, "recipientFullName must have text");

        final Context ctx = new Context();
        ctx.setLocale(locale);
        ctx.setVariable(PARAM_RECIPIENT_NAME, recipientFullName);
        ctx.setVariable(PARAM_LINK_URL, toC2SUIBaseUri(xForwardedProto, xForwardedHost, xForwardedPort));

        ctx.setVariable(PARAM_CONFIRM_VERIFICATION_HEADER, messageSource.getMessage(PROP_EMAIL_CONFIRM_VERIFICATION_BODY_HEADER, null, locale));
        ctx.setVariable(PARAM_CONFIRM_VERIFICATION_GREETING, messageSource.getMessage(PROP_EMAIL_CONFIRM_VERIFICATION_GREETING, null, locale));
        ctx.setVariable(PARAM_CONFIRM_VERIFICATION_MESSAGE1, messageSource.getMessage(PROP_EMAIL_CONFIRM_VERIFICATION_MESSAGE1, null, locale));
        ctx.setVariable(PARAM_CONFIRM_VERIFICATION_MESSAGE2, messageSource.getMessage(PROP_EMAIL_CONFIRM_VERIFICATION_MESSAGE2, null, locale));
        ctx.setVariable(PARAM_CONFIRM_VERIFICATION_LOGIN_LINK, messageSource.getMessage(PROP_EMAIL_CONFIRM_VERIFICATION_LOGIN_LINK, null, locale));
        ctx.setVariable(PARAM_CONFIRM_SIGN_OFF, messageSource.getMessage(PROP_EMAIL_CONFIRM_VERIFICATION_SIGN_OFF, null, locale));


        ctx.setVariable(PARAM_BRAND, emailSenderProperties.getBrand());

        sendEmail(ctx, email,
                PROP_EMAIL_CONFIRM_VERIFICATION_SUBJECT,
                TEMPLATE_CONFIRM_VERIFICATION_EMAIL,
                PROP_EMAIL_FROM_ADDRESS,
                PROP_EMAIL_FROM_PERSONAL,
                locale);
    }

    private void sendEmail(Context ctx, String email, String subjectPropKey, String templateName, String fromAddressPropKey, String fromPersonalPropKey, Locale locale) {
        try {
            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, ENCODING);
            message.setSubject(messageSource.getMessage(subjectPropKey, null, locale));
            message.setTo(email);
            message.setFrom(messageSource.getMessage(fromAddressPropKey, null, locale), messageSource.getMessage(fromPersonalPropKey, null, locale));
            final String htmlContent = templateEngine.process(templateName, ctx);
            message.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSenderException(e);
        }
    }

    private String toC2SUIBaseUri(String xForwardedProto, String xForwardedHost, int xForwardedPort) {
        try {
            return createURIBuilder(xForwardedProto, xForwardedHost, xForwardedPort)
                    .setPath(emailSenderProperties.getC2sUiRoute())
                    .build().toString();
        } catch (URISyntaxException e) {
            throw new EmailSenderException(e);
        }
    }

    private String toC2SUIVerificationUri(String xForwardedProto, String xForwardedHost, int xForwardedPort, String fragment) {
        try {
            return createURIBuilder(xForwardedProto, xForwardedHost, xForwardedPort)
                    .setPath(emailSenderProperties.getC2sUiRoute() + emailSenderProperties.getC2sUiVerificationRelativePath())
                    .setFragment(fragment)
                    .build()
                    .toString();
        } catch (URISyntaxException e) {
            throw new EmailSenderException(e);
        }
    }

    private URIBuilder createURIBuilder(String xForwardedProto, String xForwardedHost, int xForwardedPort) {
        final URIBuilder uriBuilder = new URIBuilder()
                .setScheme(xForwardedProto)
                .setHost(xForwardedHost);
        if (("http".equalsIgnoreCase(xForwardedProto) && xForwardedPort != 80) ||
                "https".equalsIgnoreCase(xForwardedProto) && xForwardedPort != 443) {
            uriBuilder.setPort(xForwardedPort);
        }
        return uriBuilder;
    }
}