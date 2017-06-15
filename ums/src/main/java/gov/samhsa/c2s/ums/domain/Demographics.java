package gov.samhsa.c2s.ums.domain;


import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Entity
@Data
@Audited
@ToString(exclude = {"patient", "user"})
@EqualsAndHashCode(exclude = {"patient", "user"})
@ScriptAssert(
        lang = "javascript",
        alias = "_",
        script = "!_.hasIllegalIdentifiers()",
        message = "this identifier system identifiers can only be assigned once")
public class Demographics {

    /**
     * The id.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The first name.
     */
    @NotNull
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ]+[-]?[a-zA-ZÀ-ÿ']*[a-zA-ZÀ-ÿ]$", message = "The First Name contains invalid characters. Please try again.")
    private String firstName;

    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ]+[-]?[a-zA-ZÀ-ÿ']*[a-zA-ZÀ-ÿ]$", message = "The Middle Name contains invalid characters. Please try again.")
    private String middleName;

    /**
     * The last name.
     */
    @NotNull
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ]+[-]?[a-zA-ZÀ-ÿ']*[a-zA-ZÀ-ÿ]$", message = "The Last Name contains invalid characters. Please try again.")
    private String lastName;

    /**
     * The birth day.
     */
    @Past
    private LocalDate birthDay;

    /**
     * The telephone.
     */
    @OneToMany(mappedBy = "demographics", cascade = CascadeType.ALL)
    @NotAudited
    private List<Telecom> telecoms;
    /**
     * The administrative gender code.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @NotAudited
    private AdministrativeGenderCode administrativeGenderCode;

    @OneToMany(mappedBy = "demographics", cascade = CascadeType.ALL)
    @NotAudited
    private List<Address> addresses;

    @OneToOne(mappedBy = "demographics")
    private User user;

    @OneToOne(mappedBy = "demographics")
    private Patient patient;

    @ManyToMany
    private List<Identifier> identifiers;

    public boolean hasIllegalIdentifiers() {
        return Optional.of(getIdentifiers()).orElseGet(Collections::emptyList).stream()
                .filter(identifier -> Boolean.FALSE.equals(identifier.getIdentifierSystem().getReassignable()))
                .anyMatch(identifier -> Optional.of(identifier)
                        .map(Identifier::getDemographics)
                        .map(List::size).filter(size -> size > 0).isPresent());
    }
}
