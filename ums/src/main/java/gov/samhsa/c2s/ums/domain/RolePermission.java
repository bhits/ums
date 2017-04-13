package gov.samhsa.c2s.ums.domain;

import gov.samhsa.c2s.ums.domain.valueobject.RolePermissionId;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@Data
public class RolePermission {

    @EmbeddedId
    RolePermissionId pk = new RolePermissionId();

    @Transient
    private Role role;

    @Transient
    private Permission permission;

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RolePermission that = (RolePermission) o;

        if (getPk() != null ? !getPk().equals(that.getPk())
                : that.getPk() != null)
            return false;

        return true;
    }

    public int hashCode() {
        return (getPk() != null ? getPk().hashCode() : 0);
    }
}
