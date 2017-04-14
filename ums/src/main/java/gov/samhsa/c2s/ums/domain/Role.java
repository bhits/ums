package gov.samhsa.c2s.ums.domain;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
@Audited
public class Role {
    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String roleName;


    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Scope> scopes;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy ="roles" )
    private Set<User> users;

}
