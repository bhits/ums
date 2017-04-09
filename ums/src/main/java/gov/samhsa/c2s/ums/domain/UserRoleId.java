package gov.samhsa.c2s.ums.domain;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Data
public class UserRoleId implements Serializable {

    @ManyToOne
    private User user;

    @ManyToOne
    private Role role;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRoleId that = (UserRoleId) o;

        return (user != null ? user.equals(that.user) : that.user == null) && (role != null ? role.equals(that.role) : that.role == null);
    }

    public int hashCode() {
        int result;
        result = (user != null ? user.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

}
