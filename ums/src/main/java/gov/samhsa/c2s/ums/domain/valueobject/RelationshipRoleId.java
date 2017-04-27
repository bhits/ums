package gov.samhsa.c2s.ums.domain.valueobject;

import gov.samhsa.c2s.ums.domain.Role;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Embeddable
@Data
@Builder
public class RelationshipRoleId implements Serializable{

  @OneToOne
  private Role role;
}
