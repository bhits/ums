package gov.samhsa.c2s.ums.domain;


import gov.samhsa.c2s.ums.domain.reference.CountryCode;
import gov.samhsa.c2s.ums.domain.reference.StateCode;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * The Class Address.
 */
@Entity
@Audited
@Data
public class Address {
    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The street address line.
     */
    @NotNull
    @Size(min = 1, max = 50)
    private String streetAddressLine;

    /**
     * The city.
     */
    @NotNull
    @Size(max = 30)
    private String city;

    /**
     * The state code.
     */
    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private StateCode stateCode;

    /**
     * The postal code.
     */
    @NotNull
    @Pattern(regexp = "\\d{5}(?:[-\\s]\\d{4})?")
    private String postalCode;

    /**
     * The country code.
     */
    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private CountryCode countryCode;

    @OneToMany
    @NotAudited
    List<User> Users;


}
