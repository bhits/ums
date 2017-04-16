package gov.samhsa.c2s.ums.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RelationDto {
    private long userId;
    private long patientId;
    private String relationshipCode;
}
