package gov.samhsa.c2s.ums.domain;


import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import lombok.Data;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(exclude="telecoms")
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
    private LocalDate birthDay;

    /**
     * The social security number.
     */
    @Pattern(regexp = "(\\d{3}-?\\d{2}-?\\d{4})*")
    private String socialSecurityNumber;

    private String oAuth2UserId;

    /**
     * The telephone.
     */
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @NotAudited
    private List<Telecom> telecoms;
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

    @ManyToOne
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Address address;

    private boolean isDisabled = false;
}
