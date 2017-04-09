package gov.samhsa.c2s.ums.domain;

import lombok.Data;
import org.hibernate.envers.NotAudited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Role {
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
    private String roleName;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.role", cascade = CascadeType.ALL)
    private List<UserRole> users = new ArrayList<>();

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.role", cascade = CascadeType.ALL)
    private List<RolePermission> permissions = new ArrayList<>();

}
