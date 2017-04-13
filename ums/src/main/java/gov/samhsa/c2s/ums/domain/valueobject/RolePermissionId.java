package gov.samhsa.c2s.ums.domain.valueobject;

import gov.samhsa.c2s.ums.domain.Permission;
import gov.samhsa.c2s.ums.domain.Role;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Data
public class RolePermissionId implements Serializable {

    @ManyToOne
    private Role role;

    @ManyToOne
    private Permission permission;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RolePermissionId that = (RolePermissionId) o;

        return (role != null ? role.equals(that.role) : that.role == null) && (permission != null ? permission.equals(that.permission) : that.permission == null);
    }

    public int hashCode() {
        int result;
        result = (role != null ? role.hashCode() : 0);
        result = 31 * result + (permission != null ? permission.hashCode() : 0);
        return result;
    }
}
