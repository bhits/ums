package gov.samhsa.c2s.ums.domain.reference;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The Class AbstractLocalDBLookupCodedConcept.
 */
@MappedSuperclass
@Data
public abstract class AbstractLocalDBLookupCodedConcept {
    /**
     * The id.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The code.
     */
    @NotNull
    @Size(max = 255)
    private String code;


    /**
     * The display name.
     */
    @NotNull
    @Size(max = 255)
    private String displayName;

    /**
     * The original text.
     */
    @Size(max = 255)
    private String description;

    /**
     * The code system.
     */
    @Size(max = 255)
    private String codeSystem;

    /**
     * The code system.
     */
    @Size(max = 255)
    private String codeSystemOID;


    /**
     * The code system name.
     */
    @NotNull
    @Size(max = 255)
    private String codeSystemName;


}
