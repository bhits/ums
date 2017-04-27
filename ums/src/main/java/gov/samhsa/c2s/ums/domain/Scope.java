package gov.samhsa.c2s.ums.domain;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
@Audited
public class Scope {
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
    private String scopeName;

    /**
     * The first name.
     */
    @NotNull
    private String scopeDescription;


    @ManyToMany(cascade = CascadeType.ALL, mappedBy ="scopes" )
    private Set<Role> roles;

}
