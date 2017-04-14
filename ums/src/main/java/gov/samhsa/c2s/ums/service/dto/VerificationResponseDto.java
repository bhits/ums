package gov.samhsa.c2s.ums.service.dto;

import lombok.Data;

@Data
public class VerificationResponseDto {
    private final boolean verified;
    private final String userId;

    public VerificationResponseDto(boolean verified) {
        this.verified = verified;
        this.userId = null;
    }

    public VerificationResponseDto(boolean verified, String userId) {
        this.verified = verified;
        this.userId = userId;
    }
}