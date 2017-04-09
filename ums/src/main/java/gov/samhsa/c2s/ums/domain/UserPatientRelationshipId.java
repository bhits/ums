package gov.samhsa.c2s.ums.domain;

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
