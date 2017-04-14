package gov.samhsa.c2s.ums.service;

public interface TokenGenerator {
    String generateToken();

    String generateToken(int maxLength);
}