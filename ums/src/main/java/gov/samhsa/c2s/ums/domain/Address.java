package gov.samhsa.c2s.ums.domain;


import gov.samhsa.c2s.ums.domain.reference.CountryCode;
import gov.samhsa.c2s.ums.domain.reference.StateCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * The Class Address.
 */
@Entity
@Audited
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    /**
     * The id.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The street address line.
     */
    @Size(max = 50)
    private String line1;

    @Size(max = 50)
    private String line2;

    /**
     * The city.
     */
    @Size(max = 30)
    private String city;

    /**
     * The state code.
     */
    @ManyToOne
    @NotAudited
    private StateCode stateCode;

    /**
     * The postal code.
     */
    @Pattern(regexp = "\\d{5}(?:[-\\s]\\d{4})?")
    private String postalCode;

    /**
     * The country code.
     */
    @ManyToOne
    @NotAudited
    private CountryCode countryCode;

    @Column(name="`use`")
    @Enumerated(EnumType.STRING)
    private Use use =Use.HOME;


    public enum Use{
        HOME,
        WORK
    }

    @ManyToOne
    @NotAudited
    private Demographics demographics;



}
