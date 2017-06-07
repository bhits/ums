package gov.samhsa.c2s.ums.service.dto;

import lombok.Data;

@Data
public class IdentifierSystemDto {
    private Long id;
    private String system;
    private String display;
    private String oid;
    private boolean systemGenerated;
}
