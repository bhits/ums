package gov.samhsa.c2s.ums.domain;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_activation_id", "scope_id"}))
@Audited
public class UserScopeAssignment {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private UserActivation userActivation;

    @ManyToOne
    private Scope scope;
    /**
     * Verify if scope is assign in UAA.
     */
    private boolean assigned = false;
}