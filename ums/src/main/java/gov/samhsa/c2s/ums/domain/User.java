package gov.samhsa.c2s.ums.domain;


import lombok.Data;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Set;

@Entity
@Data
@Audited
public class User {
    /**
     * The id.
     */
    @Id
    @GeneratedValue
    private Long id;

    private String userAuthId;


    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Role> roles;

    /**
     * The Locale.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @NotAudited
    private Locale locale;

    private boolean disabled = false;

    @OneToOne(cascade = CascadeType.ALL)
    private Demographics demographics;
}
