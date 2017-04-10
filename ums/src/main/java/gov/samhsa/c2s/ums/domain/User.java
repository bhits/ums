package gov.samhsa.c2s.ums.domain;


import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.domain.valueobject.Address;
import gov.samhsa.c2s.ums.domain.valueobject.Telecom;
import lombok.Data;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class User {
    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The first name.
     */
    @NotNull
    @Size(min = 2, max = 30)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ]+[-]?[a-zA-ZÀ-ÿ']*[a-zA-ZÀ-ÿ]$", message = "The First Name contains invalid characters. Please try again.")
    private String firstName;

    /**
     * The last name.
     */
    @NotNull
    @Size(min = 2, max = 30)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ]+[-]?[a-zA-ZÀ-ÿ']*[a-zA-ZÀ-ÿ]$", message = "The Last Name contains invalid characters. Please try again.")
    private String lastName;

    /**
     * The birth day.
     */
    @Past
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private Date birthDay;

    /**
     * The email.
     */
    @Pattern(regexp = "^[\\w-]+(\\.[\\w-]+)*@([a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*?\\.[a-zA-Z]{2,6}|(\\d{1,3}\\.){3}\\d{1,3})(:\\d{4})?$")
    private String email;

    /**
     * The social security number.
     */
    @Pattern(regexp = "(\\d{3}-?\\d{2}-?\\d{4})*")
    private String socialSecurityNumber;

    private String uaa_users_id;

    /**
     * The telephone.
     */
    @Embedded
    @AuditOverride(name = "telephone", isAudited = true)
    private Telecom telecom;
    /**
     * The administrative gender code.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private AdministrativeGenderCode administrativeGenderCode;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRole> roles = new ArrayList<>();

    /**
     * The Locale.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Locale locale;

    /**
     * The address.
     */
    @Embedded
    @AuditOverrides(value = {
            @AuditOverride(name = "streetAddressLine", isAudited = true),
            @AuditOverride(name = "city", isAudited = true),
            @AuditOverride(name = "stateCode", isAudited = true),
            @AuditOverride(name = "postalCode", isAudited = true)})
    private Address address;

    private boolean isDeleted = false;
}
