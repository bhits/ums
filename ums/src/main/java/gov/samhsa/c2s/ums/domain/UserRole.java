package gov.samhsa.c2s.ums.domain;

import gov.samhsa.c2s.ums.domain.valueobject.UserRoleId;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@Data
public class UserRole {

    @EmbeddedId
    UserRoleId pk = new UserRoleId();

    @Transient
    private User user;

    @Transient
    private Role role;

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        UserRole that = (UserRole) o;

        if (getPk() != null ? !getPk().equals(that.getPk())
                : that.getPk() != null)
            return false;

        return true;
    }

    public int hashCode() {
        return (getPk() != null ? getPk().hashCode() : 0);
    }
}