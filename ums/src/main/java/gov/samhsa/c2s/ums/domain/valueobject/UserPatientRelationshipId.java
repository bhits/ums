package gov.samhsa.c2s.ums.domain.valueobject;

import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.domain.Relationship;
import gov.samhsa.c2s.ums.domain.User;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Embeddable
@Data
public class UserPatientRelationshipId implements Serializable{

  @OneToOne
  private User user;

  @OneToOne
  private Patient patient;

  @OneToOne
  private Relationship relationship;

}
