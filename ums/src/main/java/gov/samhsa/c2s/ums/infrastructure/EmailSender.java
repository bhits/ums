package gov.samhsa.c2s.ums.infrastructure;

import java.util.Locale;

public interface EmailSender {
    void sendEmailWithVerificationLink(String xForwardedProto, String xForwardedHost, int xForwardedPort, String email,
                                       String emailToken, String userPreferredLocale, String recipientFullName, Locale locale);

    void sendEmailToConfirmVerification(String xForwardedProto, String xForwardedHost, int xForwardedPort, String email, String recipientFullName, Locale locale);
}