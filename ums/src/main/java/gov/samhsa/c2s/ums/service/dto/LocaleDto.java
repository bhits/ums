package gov.samhsa.c2s.ums.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocaleDto  {
    String code;

    String displayName;

    String description;

    String codeSystem;

    String codeSystemOID;


    String codeSystemName;

}
