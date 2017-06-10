package gov.samhsa.c2s.ums.service.dto;

import gov.samhsa.c2s.ums.config.UmsProperties;
import lombok.Data;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class IdentifierSystemDto {
    private Long id;
    private String system;
    private String display;
    private String oid;
    @Valid
    private Map<String, List<UmsProperties.RequiredIdentifierSystem>> requiredIdentifierSystemsByRole = new HashMap<>();
}
