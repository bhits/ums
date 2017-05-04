package gov.samhsa.c2s.ums.infrastructure;

public interface EmailSender {
    void sendEmailWithVerificationLink(String xForwardedProto, String xForwardedHost, int xForwardedPort, String email,
                                       String emailToken, String userPreferredLocale, String recipientFullName);

    void sendEmailToConfirmVerification(String xForwardedProto, String xForwardedHost, int xForwardedPort, String email, String recipientFullName);
}